package com.pi4j.io.serial;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  SerialBase.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.pi4j.io.IOBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <p>Abstract SerialBase class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public abstract class SerialBase extends IOBase<Serial, SerialConfig, SerialProvider> implements Serial {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    protected boolean isOpen = false;
    protected final CopyOnWriteArrayList<SerialDataEventListener> listeners;

    /**
     * <p>Constructor for SerialBase.</p>
     *
     * @param provider a {@link com.pi4j.io.serial.SerialProvider} object.
     * @param config a {@link com.pi4j.io.serial.SerialConfig} object.
     */
    protected SerialBase(SerialProvider provider, SerialConfig config){
        super(provider, config);
        listeners = new CopyOnWriteArrayList<>();
        logger.trace("created instance with config: {}", config);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isOpen() {
        return this.isOpen;
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public void close() {
        logger.trace("invoked 'closed()'");
        this.isOpen = false;
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
