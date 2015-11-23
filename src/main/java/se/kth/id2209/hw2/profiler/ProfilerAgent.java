package se.kth.id2209.hw2.profiler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.proto.states.MsgReceiver;
import se.kth.id2209.hw2.exhibition.Artifact;
import se.kth.id2209.hw2.util.DFUtilities;
import se.kth.id2209.hw2.util.Ontologies;

/**
 * A Profiler Agent travels around the network and looks for interesting
 * information about art and culture from online museums or art galleries on the
 * internet. This is done by interacting with a Tour Guide Agent to get a
 * personalized virtual tour and a Curator Agent to obtain detailed information
 * about each of the items stated in the virtual tour.
 *
 * @author Kim
 */
@SuppressWarnings("serial")
public class ProfilerAgent extends Agent {

    private UserProfile profile;
    private List<Integer> recommendedArtifacts;
    private List<Artifact> lookedUpArtifacts;
    static final String ACL_LANGUAGE = "Java Serialized";
    public static final String DF_NAME = "Profiler-agent";

    /**
     * Creates a user profile and registers itself to the DFService. Starts
     * sending requests for a tour.
     */
    @Override
    protected void setup() {
        super.setup();
        setRecommendedArtifacts(new ArrayList<Integer>());
        setLookedUpArtifacts(new ArrayList<Artifact>());

        List<String> interests = new ArrayList<String>();
        List<Integer> visitedItems = new ArrayList<Integer>();

        interests.add(Artifact.GENRE.PAINTING.toString());

        profile = new UserProfile(22, "Programmer", UserProfile.GENDER.male,
                interests, visitedItems);
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType(DF_NAME);
        sd.setName(getLocalName());
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        SequentialBehaviour seq = new SequentialBehaviour();
        seq.addSubBehaviour(new SendTourGuideRequestBehaviour(this, profile));
        seq.addSubBehaviour(new MsgReceiverBehaviour(ProfilerAgent.this, null, MsgReceiver.INFINITE,
                        new DataStore(), null));
        addBehaviour(seq);
    }

    /**
     * Deregisters itself from the DFService.
     */
    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        System.out.println("Agent " + getAID().getName() + " is terminating.");
    }

    UserProfile getUserProfile() {
        return profile;
    }

    List<Artifact> getLookedUpArtifacts() {
        return lookedUpArtifacts;
    }

    void setLookedUpArtifacts(List<Artifact> lookedUpArtifacts) {
        this.lookedUpArtifacts = lookedUpArtifacts;
    }

    List<Integer> getRecommendedArtifacts() {
        return recommendedArtifacts;
    }

    void setRecommendedArtifacts(List<Integer> recommendedArtifacts) {
        this.recommendedArtifacts = recommendedArtifacts;
    }
}
