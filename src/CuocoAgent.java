import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class CuocoAgent extends Agent {
    protected void setup() {
        System.out.println("Agente Cuoco " + getAID().getLocalName() + " pronto.");

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
                    Thread.sleep(5000); // Simula 3 secondi di preparazione
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Cuoco " + getAID().getLocalName()  + ": Preparazione del piatto terminata - " + piatto);
                // Invia il piatto al cameriere
                ACLMessage msgCameriere = new ACLMessage(ACLMessage.REQUEST);
                msgCameriere.setContent(piatto+":pronto");
                msgCameriere.addReceiver(new AID("cameriere1", AID.ISLOCALNAME));
                send(msgCameriere);

            } else {
                block();
            }
        }
    }
}
