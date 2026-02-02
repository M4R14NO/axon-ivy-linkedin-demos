package assistant;

import ch.ivyteam.ivy.scripting.objects.List;
import financial.tracker.part3.Transaktion;

public class TransaktionResponse {
    private List<Transaktion> transactions;
    
    public TransaktionResponse() {}
    
    public List<Transaktion> getTransactions() { 
        return transactions; 
    }
    
    public void setTransactions(List<Transaktion> transactions) { 
        this.transactions = transactions; 
    }
}