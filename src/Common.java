import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.ArrayList;
import java.util.List;

public class Common {
    //Funzione per cercare un agente cameriere libero a cui effettuare una REQUEST o un'INFORM
    static List<String> searchCameriere(Agent myAgent){
        List<String> camerieri = new ArrayList<>();
        AID[] camerieriAgents = new AID[0];
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Libero");
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(myAgent, template);
            camerieriAgents = new AID[result.length];
            for (int i = 0; i < result.length; i++) {
                camerieriAgents[i] = result[i].getName();
            }
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        for (AID camerieriAgent : camerieriAgents) {
            camerieri.add(camerieriAgent.getLocalName());
        }
        return camerieri;
    }
}
