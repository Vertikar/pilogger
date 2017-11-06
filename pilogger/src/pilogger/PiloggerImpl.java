package pilogger;

import java.io.IOException;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.SerialPortException;
import com.pi4j.wiringpi.Spi;

import probes.GeigerProbe;
import probes.I2Cprobe;
import probes.SystemProbe;
import probes.WirelessProbe;
import tests.BMP085probeSimulation;
import tests.GeigerProbeSimulation;
import tests.PowerProbeSimulation;

public class PiloggerImpl {
	private I2Cprobe bmp085Probe;
    private GeigerProbe geigerCounter;
    private SystemProbe systemProbe;
    private WirelessProbe wirelessProbe;
    private ProbeManager probeManager;
    
    public PiloggerImpl() {
    	probeManager = new ProbeManagerHeadLess();
    	
    	initI2CandBMP085probe();
    	initSystemProbe();
    }
    
    /**
     * Implementation of the Pilogger application GUI
     * Initialize links and Probes
     */
    public PiloggerImpl(PiloggerGUI gui) {
    	
    	probeManager = new ProbeManagerSwing(gui);
    	
    	if (PiloggerLauncher.simulation) {
    		probeManager.addProbe(new BMP085probeSimulation());
    		probeManager.addProbe(new GeigerProbeSimulation());
    		probeManager.addProbe(new PowerProbeSimulation());
    	} else {
    		initI2CandBMP085probe();
//    		initComAndGeigerProbe(); 
    		initSystemProbe();
//    		initSPIandWirelessProbe();
    	}
    	
    } 
    
    private void initComAndGeigerProbe() {
    	try {
    		final Serial serial = SerialFactory.createInstance();
    		geigerCounter = new GeigerProbe(serial);
    		probeManager.addProbe(geigerCounter);
    	} catch (SerialPortException e) {
    		e.printStackTrace();
    	}
    }
	private void initI2CandBMP085probe() {
    	try {
			final I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
			bmp085Probe = new I2Cprobe(bus);
			probeManager.addProbe(bmp085Probe);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	private void initSystemProbe() {
		systemProbe = new SystemProbe();
		probeManager.addProbe(systemProbe);
	}
	private void initSPIandWirelessProbe() {
		try {
			int fd = Spi.wiringPiSPISetup(0, 10000000);
			if (fd <= -1) {
				System.out.println(" ==>> SPI SETUP FAILED");
				return;
			} 
			final GpioController gpio = GpioFactory.getInstance();
			wirelessProbe = new WirelessProbe(gpio);
			probeManager.addProbe(wirelessProbe);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
}
