import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CameriereAgent extends Agent {
    static HashMap<String,String> listaOrdini = new HashMap<>();

    private void updateStatusFree(){
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        //Aggiungo il servizio di "essere libero" per poter accettare dai clienti ordinazioni
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Libero");
        sd.setName("Cameriere-Libero");
        dfd.addServices(sd);

        //Registro nella YP il fatto che il cameriere è libero e diponibile a prendere un'ordonazione
        try {
            DFService.register(this, dfd);
        }catch (FIPAException e){
            e.printStackTrace();
        }
    }

    //Rimuovo lo stato libero in quanto sono impegnato a portare dei piatti
    private void updateStatusLock(){
        try {
            DFService.deregister(this);
        }catch (FIPAException e){
            e.printStackTrace();
        }
    }

    protected void setup() {
        //Imposto nella YP lo stato libero così da poter ricevere messaggi
        updateStatusFree();

        System.out.println("Agente Cameriere " + getAID().getLocalName() + " pronto.");

        addBehaviour(new ComportamentoOrdini());
    }

    //Comportamento base dei camerieri, girano per la sala alla ricerca di ordini dai clienti
    private class ComportamentoOrdini extends CyclicBehaviour {
        public void action() {
            // Ricevi l'ordinazione dal cliente e controllo che sia una request altrimenti lascio il messaggio nella coda
            MessageTemplate mt = MessageTemplate.MatchPerformative(16);
            ACLMessage msg = receive(mt);
            if (msg != null) {
                System.out.println(msg.getContent());
                String cliente = msg.getContent().split(":")[0];
                String piatto = msg.getContent().split(":")[1];

                listaOrdini.put(cliente,piatto);

                System.out.println("Cameriere " + getAID().getLocalName() + ": Ordine ricevuto - [" + piatto + "] dal cliente "+cliente);

                updateStatusLock();
                try {
                    Thread.sleep(3000); // Simula 3 secondi per portare il piatto
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updateStatusFree();
                // Invia l'ordinazione al cuoco
                ACLMessage msgCucina = new ACLMessage(ACLMessage.REQUEST);
                msgCucina.setContent(piatto);
                msgCucina.addReceiver(new AID("cuoco", AID.ISLOCALNAME)); // Indirizzo dell'agente cuoco
                send(msgCucina);

                //Una volta consegnata l'ordinazione aggiungo il comportamento di consegnarla al cliente giusto quando sarà pronta
                addBehaviour(new ComportamentoConsegna());
            }
            else {
                block();
            }
        }
    }

    //Comportamento che viene aggiunto quando il cameriere sa che deve andare a prendere un piatto dalla cucina
    private class ComportamentoConsegna extends CyclicBehaviour {

        public void action() {
            // Ricevi il piatto pronto dal cuoco e controllo che sia una INFORM altrimenti lascio il messaggio nella coda
            MessageTemplate mt = MessageTemplate.MatchPerformative(7);
            ACLMessage msg = receive(mt);
            if (msg != null) {
                String clienteDaConsegnare = "";
                String piatto = msg.getContent().split(":")[0];

                //Otteno il nome del cliente che aveva ordinato il piatto per capire a chi consegnarlo
                for (Map.Entry<String, String> entry : listaOrdini.entrySet()) {
                    if (entry.getValue().equals(piatto)) {
                        clienteDaConsegnare = entry.getKey();
                        break;
                    }
                }

                System.out.println("Cameriere " + getAID().getLocalName() + ": Piatto ricevuto dal cuoco - [" + piatto + "], lo porto al cliente " + clienteDaConsegnare);

                updateStatusLock();
                try {
                    Thread.sleep(3000); // Simula 3 secondi per portare il piatto
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updateStatusFree();

                // Porta il piatto al Cliente
                ACLMessage msgSala = new ACLMessage(ACLMessage.INFORM);
                msgSala.setContent(piatto);
                msgSala.addReceiver(new AID(clienteDaConsegnare, AID.ISLOCALNAME)); // Indirizzo dell'agente cliente a cui appartiene il piatto
                send(msgSala);

                listaOrdini.remove(clienteDaConsegnare);
            } else {
                block();
            }
        }
    }
    protected void takeDown() {
        super.takeDown();
        System.out.println("[TERMINATO] Cameriere " + getAID().getLocalName());
    }

}
