package com.zte.rpc;

public class ServiceProducerImpl implements ServiceProducer {

    @Override
    public String sendData(String data) {
        return "I am service producer!!, the data is " + data;
    }

    @Override
    public String sendMsg(String msg) {
        return "this is new Msg :" + msg;
    }
}
