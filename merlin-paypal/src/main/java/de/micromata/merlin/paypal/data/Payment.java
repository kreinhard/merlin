package de.micromata.merlin.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.micromata.merlin.paypal.PayPalConfig;

import java.util.ArrayList;
import java.util.List;

/*
        WebProfile webProfile = new WebProfile();
        InputFields inputFields = new InputFields();
        inputFields.setNoShipping(0);
        inputFields.setAddressOverride(1);
        webProfile.setInputFields(inputFields);
        try {
            webProfile.create(config.getApiContext());
        } catch (PayPalRESTException e) {
            log.error("PayPalRESTException occurred while trying to publish web profile: " + e.getDetails() + ". webProfile=" + webProfile);
            return null;
        }*/
public class Payment {
    private String intent = "sale";
    private Payer payer = new Payer();
    private List<Transaction> transactions = new ArrayList<>();
    private String noteToPayer;
    private RedirectUrls redirectUrls = new RedirectUrls();

    /**
     * Default is "sale".
     */
    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public Payer getPayer() {
        return payer;
    }

    public Payment addTransaction(Transaction transaction) {
        transactions.add(transaction);
        return this;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    @JsonProperty(value = "note_to_payer")
    public String getNoteToPayer() {
        return noteToPayer;
    }

    public Payment setNoteToPayer(String noteToPayer) {
        this.noteToPayer = noteToPayer;
        return this;
    }

    @JsonProperty(value = "redirect_urls")
    public RedirectUrls getRedirectUrls() {
        return redirectUrls;
    }

    /**
     * This method is automatically called by {@link de.micromata.merlin.paypal.PayPalConnector#createPayment(PayPalConfig, Payment)} and
     * adds the return urls for PayPal.
     * @param config
     * @return
     */
    public Payment setConfig(PayPalConfig config) {
        redirectUrls.setConfig(config);
        return this;
    }

    /**
     * Is called internally before processing a payment for updating item currencies and calculating sums etc.
     */
    public void recalculate() {
        for (Transaction transaction : transactions) {
            transaction.getAmount().getDetails().calculateSubtotal(transaction);
            String currency = transaction.getAmount().getCurrency();
            for (Item item : transaction.getItemList().getItems()) {
                item.setCurrency(currency);
            }
        }
    }
}
