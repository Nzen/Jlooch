/* jlooch -- make them droney sounds once again, using JSyn
 *
 * 		Brad Garton, fall 2001
 *
 Fixed some outdated stuff, Nicholas Prado 2013
"Many uses of stop should be replaced by code that simply modifies some variable to indicate that the target thread should stop running. The target thread should check this variable regularly, and return from its run method in an orderly fashion if the variable indicates that it is to stop running."
changing int onoffstates[] to bool engaged[]
*/
import java.util.*;
import java.awt.*;
import java.net.*;
import java.awt.event.*;
import java.applet.Applet;
import com.softsynth.jsyn.*;

public class jlooch extends Applet implements AdjustmentListener, ActionListener
{
	Scrollbar DroneScroll;
	Scrollbar SeqScroll;
	Scrollbar WarbleScroll;
	Scrollbar BurstScroll;
	Button goButton;
	Button [] onoffs = new Button[4];
	boolean [] engaged = { true, true, true, true };
	boolean going = false;
	boolean started = false;
	myCanvas drawArea;
	//URL netimage;

	// synthesis stuff
	DroneNotes droneyThread;
	SeqNotes seqThread;
	WarbleNote warbleThread;
	BurstNote burstThread;

	public static void main(String args[])
	{
		jlooch wooflet = new jlooch();
		AppletFrame f = new AppletFrame("bark!  bark!", wooflet);
		f.setSize(210, 350);
		f.setVisible(true);
		f.test();
		f.setResizable(false);
	}

	public void init()
	{
		Font bfont;

		Color fc = new Color(0.1f, 0.7f, 0.7f); // ugh. auto to double fooled us both
		DroneScroll = new Scrollbar(Scrollbar.VERTICAL, 0, 100, 0, 110);
		DroneScroll.setLocation(20, 45);
		DroneScroll.setSize(25,200);
		DroneScroll.setName("drones");
		DroneScroll.setBackground(fc);
		DroneScroll.addAdjustmentListener(this);
		add(DroneScroll);

		fc = new Color(0.2f, 0.6f, 0.7f);
		SeqScroll = new Scrollbar(Scrollbar.VERTICAL, 79, 100, 0, 110);
		SeqScroll.setLocation(65, 45);
		SeqScroll.setSize(25,200);
		SeqScroll.setName("seqs");
		SeqScroll.setBackground(fc);
		SeqScroll.addAdjustmentListener(this);
		add(SeqScroll);

		fc = new Color(0.3f, 0.5f, 0.7f);
		WarbleScroll = new Scrollbar(Scrollbar.VERTICAL, 86, 100, 0, 110);
		WarbleScroll.setLocation(110, 45);
		WarbleScroll.setSize(25,200);
		WarbleScroll.setName("warbles");
		WarbleScroll.setBackground(fc);
		WarbleScroll.addAdjustmentListener(this);
		add(WarbleScroll);

		fc = new Color(0.4f, 0.4f, 0.7f);
		BurstScroll = new Scrollbar(Scrollbar.VERTICAL, 88, 100, 0, 110);
		BurstScroll.setLocation(155, 45);
		BurstScroll.setSize(25,200);
		BurstScroll.setName("bursts");
		BurstScroll.setBackground(fc);
		BurstScroll.addAdjustmentListener(this);
		add(BurstScroll);

		fc = new Color(0.9f, 0.1f, 0.2f);
		for (int ind = 0; ind < 4; ind++)
		{
			onoffs[ind] = new Button();
			onoffs[ind].setLocation(ind*45+27, 30);
			onoffs[ind].setSize(10, 10);
			onoffs[ind].setBackground(fc);
			onoffs[ind].setForeground(Color.yellow);
			onoffs[ind].setLabel("+");
			onoffs[ind].addActionListener(this);
			add(onoffs[ind]);
		}
		onoffs[0].setName("drones");
		onoffs[1].setName("seqs");
		onoffs[2].setName("warbles");
		onoffs[3].setName("bursts");

		fc = new Color(0.2f, 0.7f, 0.8f);
		goButton = new Button();
		goButton.setLocation(65, 270);
		goButton.setSize(90, 20);
		goButton.setBackground(fc);
		bfont = new Font("Times", Font.ITALIC, 10);
		goButton.setFont(bfont);
		fc = new Color(0.9f, 0.1f, 0.1f);
		goButton.setForeground(fc);
		goButton.setLabel("Go...");
		goButton.setName("main");
		goButton.addActionListener(this);
		add(goButton);

		/*try {
			netimage = new URL("file:./loochicon.gif");
			//netimage = new URL("http://music.columbia.edu/~brad/jlooch/loochicon.gif");
		} catch(MalformedURLException e) {
			System.err.println("no image");
			return;
		}*/
		
		drawArea = new myCanvas();
		drawArea.setBackground(Color.white);
		drawArea.setSize(210, 350);
		drawArea.setLocation(0, 0);
		add(drawArea);
	}

	public void start()
	{
		drawArea.start();

		try
		{
			Synth.startEngine(0);

			droneyThread = new DroneNotes();
			seqThread = new SeqNotes();
			warbleThread = new WarbleNote();
			burstThread = new BurstNote();
		} catch(SynthException e) {
			SynthAlert.showError(this,e);
		}
	}

	public void stop()
	{
		try
		{
			if (going) {
				if (engaged[0]) {
					droneyThread.stopSound(); }
				if (engaged[1]) {
					seqThread.stopSound(); }
				if (engaged[2]) {
					warbleThread.stopSound(); }
				if (engaged[3]) {
					burstThread.stopSound(); }
			}
			droneyThread = null;
			seqThread = null;
			warbleThread = null;
			burstThread = null;
			if (started) {
				Synth.stopEngine();
			}
		} catch(SynthException e) {
			SynthAlert.showError(this,e);
		}
	}

	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		Scrollbar theScroll = (Scrollbar)e.getAdjustable();
		int value;
		double prob;

		value =  theScroll.getValue();
		prob = (double)(100-value)/100.0;

		if (started) {
			if (theScroll.getName() == "drones") {
				droneyThread.setProb(prob);
			}
			if (theScroll.getName() == "seqs") {
				seqThread.setProb(prob);
			}
			if (theScroll.getName() == "warbles") {
				warbleThread.setProb(prob);
			}
			if (theScroll.getName() == "bursts") {
				burstThread.setProb(prob);
			}
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		int ind = 4;
		Scrollbar bType = null;
		Button theButton = (Button)e.getSource();

		if (theButton.getName() == "drones"){
			ind = 0;
			bType = DroneScroll;
		}
		else if (theButton.getName() == "seqs") {
			ind = 1;
			bType = SeqScroll;
		}
		else if (theButton.getName() == "warbles") {
			ind = 2;
			bType = WarbleScroll;
		}
		else if (theButton.getName() == "bursts") {
			ind = 3;
			bType = BurstScroll;
		}
	    try {
			if ( ind < 4 ) {
				updateBarColor( ind, bType );
			} else {
				started = true;
				Color col;
				if (going)
				{
					col = new Color(0.2f, 0.7f, 0.8f);
					goButton.setBackground(col);
					col = new Color(0.9f, 0.1f, 0.1f);
					goButton.setForeground(col);
					goButton.setLabel("Go...");

					if (engaged[0]) {
						droneyThread.stopSound();
					}
					else if (engaged[1]) {
						seqThread.stopSound();
					}
					else if (engaged[2]) {
						warbleThread.stopSound();
					}
					else if (engaged[3]) {
						burstThread.stopSound();
					}
					going = false;
				} else {
					col = new Color(0.3f, 0.7f, 0.7f);
					goButton.setBackground(col);
					col = new Color(0.9f, 0.1f, 0.1f);
					goButton.setForeground(col);
					goButton.setLabel("Stop");

					if (engaged[0]) {
						droneyThread.start();
						droneyThread.setProb((100.0-(double)DroneScroll.getValue())/100.0);
					}
					if (engaged[1]) {
						seqThread.start();
						seqThread.setProb((100.0-(double)SeqScroll.getValue())/100.0);
					}
					if (engaged[2]) {
						warbleThread.start();
						warbleThread.setProb((100.0-(double)WarbleScroll.getValue())/100.0);
					}
					if (engaged[3]) {
						burstThread.start();
						burstThread.setProb((100.0-(double)BurstScroll.getValue())/100.0);
					}
					going = true;
				}
		}
	    } catch(SynthException se) {
		SynthAlert.showError(this,se);
	    }
	}

	private void updateBarColor( int soundIndex, Scrollbar which )
	{
		Color goCol = new Color(0.9f, 0.1f, 0.2f);
		Color stopCol = new Color(0.1f, 0.8f, 0.7f);
		if (going) {
			if ( !engaged[ soundIndex ] ) {
				droneyThread.start();
				droneyThread.setProb((100.0-(double)which.getValue())/100.0);
				onoffs[ soundIndex ].setBackground(goCol);
				onoffs[ soundIndex ].setForeground(Color.yellow);
				onoffs[ soundIndex ].setLabel("+");
				engaged[ soundIndex ] = true;
			} else {
				droneyThread.stopSound();
				onoffs[ soundIndex ].setBackground(stopCol);
				onoffs[ soundIndex ].setForeground(Color.white);
				onoffs[ soundIndex ].setLabel("-");
				engaged[ soundIndex ] = false;
			}
		} else {
			if ( !engaged[ soundIndex ] ) {
				onoffs[ soundIndex ].setBackground(goCol);
				onoffs[ soundIndex ].setForeground(Color.yellow);
				onoffs[ soundIndex ].setLabel("+");
				engaged[ soundIndex ] = true;
			} else {
				onoffs[ soundIndex ].setBackground(stopCol);
				onoffs[ soundIndex ].setForeground(Color.white);
				onoffs[ soundIndex ].setLabel("-");
				engaged[ soundIndex ] = false;
			}
		}
	}

}

class myCanvas extends Canvas
{
	Graphics cg,bg;
	Speckle goDots;
	Image bstore;
	//Image loochimage;	// cut all these out as I have read access problems
	MediaTracker tracker = new MediaTracker(this);

	public myCanvas()//URL db)
	{
		super();
	//	loochimage = Toolkit.getDefaultToolkit().getImage(db);
		goDots = new Speckle(210, 350);
	}

	public void start()
	{
		bstore = createImage(210, 350);
		tracker.addImage(bstore, 0);
		//tracker.addImage(loochimage, 1);
		try {
			tracker.waitForID(0);
			tracker.waitForID(1);
		} catch (InterruptedException e) {
			System.err.println(e);
		}

		cg = this.getGraphics();
		bg = bstore.getGraphics();
		goDots.setGraphics(cg, bg);
		goDots.start();
	}

	public void paint(Graphics g)
	{
		Font lfont;

		// no idea why I have to do this... the MediaTracker
		// should catch these errors, I thought
		while ( (bstore == null) )//|| (loochimage == null) )
		{
			try {
				java.lang.Thread.sleep(10);
			} catch (InterruptedException e) {
				return;
			}
		}
		g.drawImage(bstore, 0, 0, Color.white, this);
	//	g.drawImage(loochimage, 3, 297, Color.white, this);

		lfont = new Font("Times", Font.PLAIN, 10);
		g.setFont(lfont);
		g.setColor(Color.black);
		g.drawString("drones", 18, 260);
		g.drawString("sequences", 55, 260);
		g.drawString("warbles", 105, 260);
		g.drawString("noises", 155, 260);
		lfont = new Font("Times", Font.BOLD, 14);
		g.setFont(lfont);
		g.drawString("Spirit of the Looch", 40, 20);
		lfont = new Font("Helvetica", Font.PLAIN, 8);
		g.setFont(lfont);
		g.drawString("Brad Garton", 145, 317);
	}
}

