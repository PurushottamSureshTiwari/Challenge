package com.db.awmd.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;


@Data
@Getter
@Setter
public class BalanceTransfer {

  @NotNull
  @NotEmpty
  private final String accountFromId;

  @NotNull
  @NotEmpty
  private final String accountToId;



  @NotNull(message = "Amount must be not null")
  @Positive(message = "Amount must be Positive")
  private BigDecimal amount;



  @JsonCreator
  public BalanceTransfer(@JsonProperty("accountFromId") String accountFromId,@JsonProperty("accountToId") String accountToId,
                         @JsonProperty("amount") BigDecimal amount) {
    this.accountFromId = accountFromId;
    this.accountToId = accountToId;
    this.amount = amount;
  }
}
