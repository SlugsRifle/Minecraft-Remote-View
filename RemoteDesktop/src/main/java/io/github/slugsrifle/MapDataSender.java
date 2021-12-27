package io.github.slugsrifle;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;


public class MapDataSender {
    static byte[][] datas = new byte[Main.getSize()][16384];
    private ProtocolManager protocolManager;
    ExecutorService es = Executors.newFixedThreadPool(2);

    public MapDataSender(ProtocolManager protocolManager) {
        this.protocolManager = protocolManager;
    }

    public void exit() {
        es.shutdownNow();
        while (!es.isShutdown()) ;
    }

    public void sendPacket() {
        ArrayList<Future<?>> l = new ArrayList<>();
        for (int i = 0; i < Main.getSize(); ++i) {
            l.add(es.submit(new Worker(i)));
        }
        l.stream().forEach(x -> {
            while (!x.isDone()) ;
        });
    }

    class Worker implements Runnable {
        int i;

        public Worker(int i) {
            this.i = i;
        }

        @Override
        public void run() {
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.MAP);
            try {
                packet.getIntegers().write(0, i);
                packet.getBytes().write(0, (byte) 0);
                packet.getBooleans().write(0, false);
                InternalStructure map = packet.getStructures().read(1);
                map.getIntegers()
                        .write(0,0)
                        .write(1,0)
                        .write(2,128)
                        .write(3,128);
                map.getByteArrays().write(0,datas[i]);

            } catch (Exception e) {
                e.printStackTrace();
            }
            protocolManager.broadcastServerPacket(packet);
//            Bukkit.getOnlinePlayers().forEach(player -> {
//                try {
//                    protocolManager.sendServerPacket(player, packet);
//                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                }
//            });
        }
    }
}
