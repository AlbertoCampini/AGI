import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ClienteAgent extends Agent {
    Random random = new Random();
    List<String> menu = new ArrayList<String>(Arrays.asList("Pasta al pomodoro", "Pizza", "Patatine"));
    protected void setup() {
        System.out.println("Agente Cliente " + getAID().getLocalName()  + " pronto.");

        addBehaviour(new ComportamentoOrdinazione());
    }

    private class ComportamentoOrdinazione extends Behaviour {
        public void action() {
            // Effettua l'ordinazione
            String piatto = menu.get(random.nextInt(menu.size()));
            System.out.println("Cliente " + getAID().getLocalName()  + " ha ordinato ["+piatto+"]");
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.setContent(getAID().getLocalName() +":"+piatto); // Esempio di ordinazione
            msg.addReceiver(new AID("cameriere1", AID.ISLOCALNAME)); // Indirizzo dell'agente cameriere
            send(msg);

            //Aggiungo un comportamento ciclico per aspettare il piatto dal cameriere
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

                System.out.println("[Terminato] Cliente " + getAID().getLocalName()  + ": Ordine ricevuto - [" + piatto +"] ");

            } else {
                block();
            }
        }
    }

}
