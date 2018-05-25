package ControleSoftware;

import com.fazecast.jSerialComm.SerialPort;

public class SerialPortWrapper {
    private final SerialPort _port;

    public SerialPortWrapper(SerialPort port) {
        _port = port;
    }

    public SerialPort getPort() {
        return _port;
    }

    @Override
    public String toString() {
        return _port.getDescriptivePortName();
    }
}
