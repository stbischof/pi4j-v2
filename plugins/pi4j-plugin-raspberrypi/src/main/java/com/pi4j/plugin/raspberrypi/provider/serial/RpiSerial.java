package com.pi4j.plugin.raspberrypi.provider.serial;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: RaspberryPi Platform & Providers
 * FILENAME      :  RpiSerial.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import com.pi4j.io.serial.*;

import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <p>RpiSerial class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class RpiSerial extends SerialBase implements Serial {

    protected final CopyOnWriteArrayList<SerialDataEventListener> listeners;

    /**
     * <p>Constructor for RpiSerial.</p>
     *
     * @param provider a {@link com.pi4j.io.serial.SerialProvider} object.
     * @param config   a {@link com.pi4j.io.serial.SerialConfig} object.
     */
    public RpiSerial(SerialProvider provider, SerialConfig config) {
        super(provider, config);
        listeners = new CopyOnWriteArrayList<>();
    }

    @Override
    public void open() {
        /*
        CODE FROM V1
        // open serial port
        fileDescriptor = com.pi4j.jni.Serial.open(device, baud, dataBits, parity, stopBits, flowControl);

        // read in initial buffered data (if any) into the receive buffer
        int available = com.pi4j.jni.Serial.available(fileDescriptor);

        if(available > 0) {
            byte[] initial_data = com.pi4j.jni.Serial.read(fileDescriptor, available);
            if (initial_data.length > 0) {
                try {
                    // write data to the receive buffer
                    receiveBuffer.write(initial_data);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // create a serial data listener event for data receive events from the serial device
        SerialInterrupt.addListener(fileDescriptor, new SerialInterruptListener() {
            @Override
            public void onDataReceive(SerialInterruptEvent event) {

                // ignore any event triggers that are missing data
                if(event.getLength() <= 0) return;

                try {
                    SerialDataEvent sde = null;

                    if(isBufferingDataReceived()) {
                        // stuff event data payload into the receive buffer
                        receiveBuffer.write(event.getData());

                        //System.out.println("BUFFER SIZE : " + receiveBuffer.capacity());
                        //System.out.println("BUFFER LEFT : " + receiveBuffer.remaining());
                        //System.out.println("BUFFER AVAIL: " + receiveBuffer.available());

                        // create the serial data event; since we are buffering data
                        // it will be located in the receive buffer
                        sde = new SerialDataEvent(SerialImpl.this);
                    }
                    else{
                        // create the serial data event; since we are NOT buffering data
                        // we will pass the specific data payload directly into the event
                        sde = new SerialDataEvent(SerialImpl.this, event.getData());
                    }

                    // add a new serial data event notification to the thread pool for *immediate* execution
                    // we notify the event listeners on a separate thread to prevent blocking the native monitoring thread
                    if(!listeners.isEmpty() && isOpen()) {
                        // don't add event if executor has been shutdown or terminated
                        if(!executor.isTerminated() && !executor.isShutdown()) {
                            try {
                                executor.execute(new SerialDataEventDispatchTaskImpl(sde, listeners));
                            }
                            catch(java.util.concurrent.RejectedExecutionException e){
                                // do nothing, we are most likely in a shutdown
                            }
                        }
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // ensure file descriptor is valid
        if (fileDescriptor == -1) {
            throw new IOException("Cannot open serial port");
        }
         */
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int write(byte b) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int write(byte[] data, int offset, int length) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] buffer, int offset, int length) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int available() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void addListener(SerialDataEventListener... listener) {
        // add the new listener to the list of listeners
        Collections.addAll(listeners,listener);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void removeListener(SerialDataEventListener... listener) {
        // remove the listener from the list of listeners
        for (SerialDataEventListener lsnr : listener) {
            listeners.remove(lsnr);
        }
    }
}
