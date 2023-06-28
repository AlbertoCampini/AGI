import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CuocoAgent extends Agent {
    Random random = new Random();
    protected void setup() {

        System.out.println("Agente Cuoco " + getAID().getLocalName() + " pronto.");
        List<String> camerieri = new ArrayList<>();
        addBehaviour(new ComportamentoCucina());
    }



    private class ComportamentoCucina extends CyclicBehaviour {
        public void action() {
            // Ricevi l'ordinazione dal cameriere
            ACLMessage msg = receive();
            if (msg != null) {
                String piatto = msg.getContent();
                System.out.println("Cuoco " + getAID().getLocalName()  + ": Preparazione del piatto - " + piatto);

                // Simula il tempo di preparazione del piatto
                try {
                    Thread.sleep(5000); // Simula 5 secondi di preparazione
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //Cerco un cameriere libero
                List<String> camerieri = Common.searchCameriere(getAgent());
                while (camerieri.size() == 0){
                    // Aspetta che un camerieri si liberi
                    try {
                        Thread.sleep(2000); // Simula 5 secondi di preparazione
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    camerieri = Common.searchCameriere(getAgent());
                }
                String cameriere = camerieri.get(random.nextInt(camerieri.size()));

                try{
                    System.out.println("Cuoco " + getAID().getLocalName()  + ": Preparazione del piatto terminata - " + piatto);
                    // Invia il piatto al cameriere
                    ACLMessage msgCameriere = new ACLMessage(ACLMessage.INFORM);
                    msgCameriere.setContent(piatto+":pronto");
                    msgCameriere.addReceiver(new AID(cameriere, AID.ISLOCALNAME));
                    send(msgCameriere);
                } catch (Exception e){
                    System.out.println(cameriere);
                }


            } else {
                block();
            }
        }
    }
    protected void takeDown() {
        super.takeDown();
        System.out.println("[TERMINATO] Cuoco " + getAID().getLocalName());
    }
}
