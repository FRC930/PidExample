import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;


public class PID {
	private double kP;
	private double kI;
	private double kD;
	
	private float setPoint;
	private float lastInput;
	private double iTerm;
	
	private long lastTime;
	
	private double outMin;
	private double outMax;
	
	private double output;
	private boolean inAuto;
	
	private static final int SAMPLE_TIME = 2;
	
	public PID(double kP, double kI, double kD, double min, double max) {
		this.kP = kP;
		this.kI = kI * SAMPLE_TIME;
		this.kD = kD / SAMPLE_TIME;
		
		lastInput = 0;
		iTerm = 0;
		inAuto = true;
		
		if(min > max) return;
		outMin = min;
		outMax = max;
	}
	
	public void set(double value) {
		this.setPoint = (float) value;
		lastTime = System.currentTimeMillis();
		for(int wait = 0; wait <= 1000000; wait++) {}
	}
	
	public boolean compute(double curVal) {
		if(!inAuto) return false;
		long now = System.currentTimeMillis();
		long timeChange = now - lastTime;
		
		if(timeChange >= SAMPLE_TIME) {
			System.out.println(curVal);
			
			float error = (float) (setPoint - curVal);
			iTerm += kI * error;
			
			if(iTerm > outMax) {
				iTerm = outMax;
			}else if(iTerm < outMin) {
				iTerm = outMin;
			}
			
			float dInput = (float) (curVal - lastInput);
			lastInput = (float) curVal;
			lastTime = now;
			
			output = kP * error + iTerm - kD * dInput;
			if(output > outMax) {
				output = outMax;
			}else if(output < outMin) {
				output = outMin;
			}
			
			System.out.println("OUTPUT: " + output);
			return true;
		}
		
		return false;
	}
	
	public void setInAuto(boolean inAuto) {
		this.inAuto = inAuto;
	}
	
	public double getOutput() {
		return this.output;
	}
	
	private static void createAndShowGui() {
        List<Double> scores = new ArrayList<>();

        PID pid = new PID(.5,.4,.8,-10,10);
		pid.set(25);
		double i = 0;
        
        for (int counter = 0; counter < 10000; counter++) {
        	if(counter == 5000) {
//            	pid.set(12);
            }
        	
            if(pid.compute(i)) {
            	i += (pid.getOutput()-2);
            	scores.add(i);
            }
            
            System.out.println(counter);
        }
        
        GraphPanel mainPanel = new GraphPanel(scores);
        mainPanel.setPreferredSize(new Dimension(800, 600));
        JFrame frame = new JFrame("DrawGraph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            createAndShowGui();
         }
      });
   }
}
