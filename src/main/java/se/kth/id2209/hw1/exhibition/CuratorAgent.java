package se.kth.id2209.hw1.exhibition;

import java.util.ArrayList;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import se.kth.id2209.hw1.exhibition.Artifact.GENRE;
import se.kth.id2209.hw1.profiler.ProfilerAgent;
import se.kth.id2209.hw1.smartmuseum.TourGuideAgent;

/**
 * ïƒ˜ Curator Agent monitors the gallery/museum.
 * ïƒ˜ A gallery/museum contains detailed information of artifacts such as:
 *      ï‚§ id, name, creator, date of creation, place of creation, genre etc.
 * 
 * TODO: Implement behaviours. 
 *       Behaviors should correspond to each category below:
 *          ï�± Simple Behavior (at least 5 different behaviors):
 *              â€“ CyclicBehaviour, MsgReceiver, OneShotBehaviour,
 *                SimpleAchieveREInitiator, SimpleAchieveREResponder,
 *                TickerBehaviour, WakerBehaviour
 *          ï�± Composite Behaviors (at least 2 different behaviors):
 *              â€“ ParallelBehaviour, FSMBehaviour, SequentialBehaviour
 * 
 * @author Kim
 */

@SuppressWarnings("serial")
public class CuratorAgent extends Agent {
	private ProfilerAgent pAgent; // temporary - register at DF instead
	private TourGuideAgent tgAgent; // temporary - register at DF instead
	private ArtGallery artGallery;
	private Behaviour behaviour;

	protected void setup() {
		artGallery = artGallery.getInstance();

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());

		ServiceDescription sd = new ServiceDescription();
		sd.setType("Curator-agent");
		sd.setName(getLocalName());
		dfd.addServices(sd);

		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		behaviour = new ListenerBehaviour(this);
		addBehaviour(behaviour);
                //waker b to check db
                
	}
        
        private class DatabaseChecker extends WakerBehaviour {

            public DatabaseChecker(Agent a, long timeout) {
                super(a, timeout);
            }
        
            @Override
            public void onWake() {
                
            }
        }
        

	public void action() {
		behaviour.action();		
	}  

	public Artifact getArtifact(int id) {
		return artGallery.getArtifact(id);
	}

	@Override
	protected void takeDown() {
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		System.out.println("Agent " + getAID().getName() + " is terminating.");
	}

	public ArrayList<Integer> getArtifactIdList(GENRE genre) {		
		return artGallery.getArtifactList(genre);
	}
}
