package com.example.demo.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Uint;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import rx.functions.Action1;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 * @author anonymity
 * @create 2018-12-29 20:44
 **/
@Slf4j
@Component
public class TestTask {

    private boolean flag = false;

    @Scheduled(fixedRate = 5000)
    public void test() {
        if (flag) {
            return;
        }

        try {
            Event event = new Event("function",
                    Arrays.<TypeReference<?>>asList(
                            new TypeReference<Address>(true) {
                            },
                            new TypeReference<Uint>() {
                            },
                            new TypeReference<Bytes32>() {
                            },
                            new TypeReference<Address>(true) {
                            },
                            new TypeReference<Uint>() {
                            },
                            new TypeReference<Uint>() {
                            }));
            // start、end、contractAddress
            EthFilter ethFilter = new EthFilter(DefaultBlockParameter.valueOf(BigInteger.valueOf(6974541)), DefaultBlockParameterName.LATEST, "contract address");
            String encode = EventEncoder.encode(event);
            ethFilter.addSingleTopic(encode);

            Web3j web3j = getWeb3jWebSocketClient();
            if (null == web3j) {
                return;
            }

            web3j.ethLogObservable(ethFilter).subscribe(new Action1<Log>() {
                @Override
                public void call(Log log) {
                    EventValues eventValues = Contract.staticExtractEventParameters(event, log);

                    List<Type> indexedValues = eventValues.getIndexedValues();
                    List<Type> nonIndexedValues = eventValues.getNonIndexedValues();

                    String address = (String) indexedValues.get(0).getValue();
                    System.err.println(address);

                    byte[] byteName = (byte[]) nonIndexedValues.get(1).getValue();
                    String name = byteToString(byteName);
                    System.err.println(name);

                }
            });

            flag = true;
        } catch (Exception e) {
            flag = false;
            log.error("event error", e);
        }
    }

    private Web3j getWeb3jWebSocketClient() {
        try {
//            WebSocketClient webSocketClient = new WebSocketClient(new URI("wss://mainnet.infura.io/ws"));
//            WebSocketService webSocketService = new WebSocketService(webSocketClient, true);
//            webSocketService.connect();
            return Web3j.build(new HttpService("http://35.201.196.131:9999"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String byteToString(byte[] bytes) {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] != 0) {
                strBuilder.append((char) bytes[i]);
            } else {
                break;
            }

        }
        return strBuilder.toString();
    }
}
