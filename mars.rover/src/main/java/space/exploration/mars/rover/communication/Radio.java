package space.exploration.mars.rover.communication;

import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.forkjoin.ThreadLocalRandom;
import space.exploration.mars.rover.InstructionPayloadOuterClass.InstructionPayload;
import space.exploration.mars.rover.animation.RadioAnimationEngine;
import space.exploration.mars.rover.kernel.Rover;
import space.exploration.mars.rover.utils.RoverUtil;

import java.util.Properties;

/**
 * Created by sanketkorgaonkar on 4/27/17.
 */
public class Radio {
    private RadioAnimationEngine radioAnimEngine = null;
    private Rover                rover           = null;
    private Transmitter          transmitter     = null;
    private Receiver             receiver        = null;
    private Logger               logger          = LoggerFactory.getLogger(Radio.class);

    public Radio(Properties comsConfig, Rover rover) {
        this.transmitter = new Transmitter(comsConfig);
        this.rover = rover;
        this.receiver = new Receiver(comsConfig, this);
        receiver.start();

        RoverUtil.roverSystemLog(logger, "Radio configured:: " + RoverUtil.getPropertiesAsString(comsConfig), "INFO");
    }

    public void receiveMessage(InstructionPayload instructionPayload) {
        try {
            Thread.sleep(getComsDelaySecs());
            this.radioAnimEngine = new RadioAnimationEngine(rover.getMarsConfig(), rover.getMarsArchitect()
                    .getMarsSurface(), rover.getMarsArchitect().getRobot(), false);
            radioAnimEngine.activateRadio();
            rover.receiveMessage(instructionPayload.toByteArray());
        } catch (Exception e) {
            System.out.println("Radio receive operation has an exception");
            logger.error(e.getMessage());
        }
    }

    public void sendMessage(byte[] message) {
        try {
            this.radioAnimEngine = new RadioAnimationEngine(rover.getMarsConfig(), rover.getMarsArchitect()
                    .getMarsSurface(), rover.getMarsArchitect().getRobot(), true);
            radioAnimEngine.activateRadio();
            Thread.sleep(getComsDelaySecs());
            transmitter.transmitMessage(message);
        } catch (InvalidProtocolBufferException e) {
            System.out.println("Transmit module is in exception - invalidProtocolBuffer ");
            logger.error("InvalidProtocolBufferException error - common guys send me a good message!");
            logger.error(e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Transmit module is in exception - interruptedException ");
            logger.error("InterruptedException");
            logger.error(e.getMessage());
        }
    }

    public void reportPowerUsage(int powerUnits) {
        rover.powerCheck(powerUnits);
    }

    public Receiver getReceiver() {
        return this.receiver;
    }

    private int getComsDelaySecs() {
        return ThreadLocalRandom.current().nextInt(3000, 22000);
    }
}
