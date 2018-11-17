package de.micromata.merlin.paypal.sdk;

import com.paypal.api.payments.*;
import com.paypal.base.rest.PayPalRESTException;
import de.micromata.merlin.paypal.PaypalConfig;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PaymentCreator {
    public static Payment prepare(PaypalConfig config, Transaction... transactions) {
        // Set payer details
        Payer payer = new Payer();
        payer.setPaymentMethod(config.getDefaultPayment());

        // Set redirect URLs
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(config.getCancelUrl());
        redirectUrls.setReturnUrl(config.getReturnUrl());

        // Add transaction to a list
        List<Transaction> transactionList = new ArrayList<>();
        for (Transaction transaction : transactions) {
            transactionList.add(transaction);
        }

        // Add payment details
        Payment payment = new Payment();
        payment.setIntent(config.getDefaultIntent());
        payment.setPayer(payer);
        payment.setRedirectUrls(redirectUrls);
        payment.setTransactions(transactionList);
        return payment;
    }

    /**
     * Creates the remote payment (publish to Paypal).
     * @param payment
     */
    public static void create(PaypalConfig config, Payment payment) {
        // Create payment
        try {
            Payment createdPayment = payment.create(config.getApiContext());

            Iterator<Links> links = createdPayment.getLinks().iterator();
            while (links.hasNext()) {
                Links link = links.next();
                if (link.getRel().equalsIgnoreCase("approval_url")) {
                    // Redirect the customer to link.getHref()
                }
            }
        } catch (PayPalRESTException e) {
            System.err.println(e.getDetails());
        }
    }
}