package com.midas.app.providers.external.stripe;

import com.midas.app.models.Account;
import com.midas.app.providers.payment.CreateAccount;
import com.midas.app.providers.payment.PaymentProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter
public class StripePaymentProvider implements PaymentProvider {
  private final Logger logger = LoggerFactory.getLogger(StripePaymentProvider.class);

  private final StripeConfiguration configuration;

  /** providerName is the name of the payment provider */
  @Override
  public String providerName() {
    return "stripe";
  }

  /**
   * createAccount creates a new account in the payment provider.
   *
   * @param details is the details of the account to be created.
   * @return Account
   */
 @RequestMapping("/createCustomer")
  @Override
  public Account createAccount(@RequestBody CreateAccount details) {
    Map<String, Object> params = new HashMap<>();
    params.put("name", details.getFirstName() + " " + details.getLastName());
    params.put("email", details.getEmail());
    try {
      Customer customer = Customer.create(params);
      details.setProviderId(customer.getId());
    } catch (StripeException e) {
      throw new RuntimeException(e);
    }
    Account account =
        Account.builder()
            .firstName(details.getFirstName())
            .lastName(details.getLastName())
            .email(details.getEmail())
            .providerId(details.getProviderId())
            .build();
    return account;
  }

  @RequestMapping("/updateCustomer")
  @Override
  public Account updateAccount(@RequestBody UpdateAccount details) {
    try {
      Customer customer = Customer.retrieve(details.getCustomerId());
      Map<String, Object> params = new HashMap<>();
      String[] customerName = customer.getName().split("\\s+");
      if (details.getFirstName() != null) {
        params.put(
            "name",
            details.getFirstName() != null
                ? details.getFirstName()
                : customerName[0] + " " + details.getLastName() != null
                    ? details.getLastName()
                    : customerName[1]);
      }
      if (details.getEmail() != null) {
        params.put("email", details.getEmail());
      }
      customer.update(params);
    } catch (StripeException e) {
      throw new RuntimeException(e);
    }
    Account account =
        Account.builder()
            .firstName(details.getFirstName())
            .lastName(details.getLastName())
            .email(details.getEmail())
            .providerId(details.getCustomerId())
            .build();
    return account;
  }
}
