import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ClienteAgent extends Agent {
    Random random = new Random();
    List<String> menu = new ArrayList<>(Arrays.asList("Pasta al pomodoro", "Pizza", "Patatine"));

    protected void setup() {
        System.out.println("Agente Cliente " + getAID().getLocalName() + " pronto.");

        addBehaviour(new ComportamentoOrdinazione());
    }


    private class ComportamentoOrdinazione extends Behaviour {
        public void action() {
            //Cerco un cameriere libero
            List<String> camerieri = Common.searchCameriere(getAgent());
            String cameriere = camerieri.get(random.nextInt(camerieri.size()));

            // Scelgo dal menu cosa voglio
            String piatto = menu.get(random.nextInt(menu.size()));


            //Effettuo l'ordinazione verso un cameriere disponibile
            System.out.println("Cliente " + getAID().getLocalName() + " ha ordinato [" + piatto + "]");
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.setContent(getAID().getLocalName() + ":" + piatto); // Esempio di ordinazione
            msg.addReceiver(new AID(cameriere, AID.ISLOCALNAME)); // Indirizzo dell'agente cameriere scelto
            send(msg);

            //Aggiungo un comportamento ciclico per aspettare il piatto dal cameriere per mangiarlo
            addBehaviour(new ComportamentoAspettarePiatto());
        }

        public boolean done() {
            return true;
        }
    }

    private class ComportamentoAspettarePiatto extends CyclicBehaviour {
        public void action() {
            // Ricevi l'ordinazione dal cameriere
            ACLMessage msg = receive();
            if (msg != null) {
                String piatto = msg.getContent();

                System.out.println("Cliente " + getAID().getLocalName() + ": Ordine ricevuto - [" + piatto + "] ");

                takeDown();
            } else {
                block();
            }
        }
    }

    @Override
    protected void takeDown() {
        super.takeDown();
        System.out.println("[TERMINATO] Cliente " + getAID().getLocalName());
    }
}
