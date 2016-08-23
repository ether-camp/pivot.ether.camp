package com.ethercamp.pivot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j(topic = "controller")
@RequestMapping("/")
public class PortController {

    @RequestMapping("/")
    @ResponseBody
    String index() {
        return "Pivot.ether.camp";
    }

    /**
     * Check port connectivity of server which called us.
     * Block current thread with IO.
     */
    @CrossOrigin    // allow method to be called from client side out of this domain
    @RequestMapping(path = "/checkPort", method = RequestMethod.POST)
    Object check(@RequestBody CheckPortData input,
                 HttpServletRequest request,
                 HttpServletResponse response) {
        String ipAddress = request.getRemoteAddr();
        log.info("check " + input + ",  ipAddress:" + ipAddress);

        final Map<String, Object> result = new HashMap<>();
        result.put("result", false);

        if (input.protocol.equalsIgnoreCase("udp")) {
            // UDP
            try {
                final InetAddress address = InetAddress.getByName(ipAddress);
                final DatagramSocket ds = new DatagramSocket();
//                socket.connect(address, input.port);
                final byte [] bytes = new byte[128];
                DatagramPacket dp = new DatagramPacket(bytes, bytes.length, address, input.port);
                ds.setSoTimeout(1000);
                ds.send(dp);
                dp = new DatagramPacket(bytes, bytes.length);
                ds.receive(dp);
                ds.close();

                result.put("result", true);
            } catch (SocketException e) {
                log.info("Problem connecting UDP to " + ipAddress + ":" + input.port + " " + e.getMessage());
            } catch (UnknownHostException e) {
                log.info("Problem using address to " + ipAddress + ":" + input.port + " " + e.getMessage());
            } catch (IOException e) {
                log.info("Problem sending UDP packet to " + ipAddress + ":" + input.port + " " + e.getMessage());
            }
        } else {
            // TCP
            Socket socket = null;
            try {
                socket = new Socket(ipAddress, input.port);
                log.info("Success connecting TCP to " + ipAddress + ":" + input.port);
                result.put("result", true);
            } catch (IOException e) {
                log.info("Problem connecting TCP to " + ipAddress + ":" + input.port + " " + e.getMessage());
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        log.error("Problem closing connection", e);
                    }
                }
            }
        }

        return result;
    }
}
