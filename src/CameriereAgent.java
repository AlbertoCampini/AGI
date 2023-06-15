import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CameriereAgent extends Agent {
    HashMap<String,String> listaOrdini = new HashMap<>();

    protected void setup() {
        System.out.println("Agente Cameriere " + getAID().getLocalName() + " pronto.");

        addBehaviour(new ComportamentoOrdini());
    }

    private class ComportamentoOrdini extends CyclicBehaviour {
        public void action() {
            // Ricevi l'ordinazione dal cliente
            ACLMessage msg = receive();
            if (msg != null && !msg.getContent().contains("pronto")) {
                System.out.println(msg.getContent());
                String cliente = msg.getContent().split(":")[0];
                String piatto = msg.getContent().split(":")[1];

                listaOrdini.put(cliente,piatto);

                System.out.println("Cameriere " + getAID().getLocalName() + ": Ordine ricevuto - [" + piatto + "] dal cliente "+cliente);

                // Invia l'ordinazione al cuoco
                ACLMessage msgCucina = new ACLMessage(ACLMessage.REQUEST);
                msgCucina.setContent(piatto);
                msgCucina.addReceiver(new AID("cuoco", AID.ISLOCALNAME)); // Indirizzo dell'agente cuoco
                send(msgCucina);

            } else if(msg != null && msg.getContent().contains("pronto")){
                String clienteDaConsegnare = "";
                String piatto = msg.getContent().split(":")[0];

                //Otteno il nome del cliente che aveva ordinato il piatto
                for (Map.Entry<String, String> entry : listaOrdini.entrySet()) {
                    if (entry.getValue().equals(piatto)) {
                        clienteDaConsegnare = entry.getKey();
                        break;
                    }
                }

                System.out.println("Cameriere " + getAID().getLocalName() + ": Piatto ricevuto dal cuoco - [" + piatto + "], lo porto al cliente "+clienteDaConsegnare);

                // Invia l'ordinazione al cuoco
                ACLMessage msgSala = new ACLMessage(ACLMessage.REQUEST);
                msgSala.setContent(piatto);
                msgSala.addReceiver(new AID(clienteDaConsegnare, AID.ISLOCALNAME)); // Indirizzo dell'agente cuoco
                send(msgSala);

                listaOrdini.remove(clienteDaConsegnare);
            }
            else {
                block();
            }
        }
    }

}
