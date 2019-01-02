package com.example.demo;

import com.example.demo.common.OkHttpClientHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class DemoApplicationTests {

    /**
     * 压测，准备100个线程，等待，然后全部创建完毕，同时执行
     */
    public static void main(String[] args) {


        CyclicBarrier cb = new CyclicBarrier(100, new Runnable() {
            @Override
            public void run() {
                //一旦所有线程准备就绪，这个动作就执行
                System.out.println("准备就绪，开始！");
//                sendPost();
            }
        });

        for (int i = 1; i <= 100; i++) {
            Thread sporter = new Thread(new Task(cb), i + "号");
            sporter.start();
        }
    }



    private static class Task implements Runnable {

        private CyclicBarrier barrier;

        public Task(CyclicBarrier barrier) {
            this.barrier = barrier;
        }

        @Override
        public void run() {
            try {
                System.out.println(Thread.currentThread().getName() + " 准备就绪");
                barrier.await();

                String json = sendPost();
                System.out.println(Thread.currentThread().getName() + " 起跑-->" + json);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private static String sendPost(){
        String url = "rpc node url";
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        header.put("fortest", "");

        List<Object> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("from","0x72eA4d993bEe81fCa15589d9C0BE99c47258bB0D");
        map.put("to","0x8F99f6c478FC67A1E5d1Ceaa8F00f41c3C50ce70");
        map.put("gas","");
        map.put("gasPrice","0x9184e72a000");
        map.put("value","0x27F7D0BDB92000000");
        map.put("data","");
        list.add(map);

        Map<String, Object> params = new HashMap<>();
        params.put("jsonrpc","2");
        params.put("method","eth_estimateGas");
        params.put("params",list);
        params.put("id","1");
        String json = OkHttpClientHelper.post(url, header, params);
       return json;
    }
}

