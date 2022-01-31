package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.BalanceTransfer;
import com.db.awmd.challenge.exception.AccountNotExistException;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.db.awmd.challenge.exception.NegativeBalanceException;
import org.springframework.stereotype.Repository;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

  private final Map<String, Account> accounts = new ConcurrentHashMap<>();

  @Override
  public void createAccount(Account account) throws DuplicateAccountIdException {
    Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
    if (previousAccount != null) {
      throw new DuplicateAccountIdException(
        "Account id " + account.getAccountId() + " already exists!");
    }
  }

  @Override
  public Account getAccount(String accountId) {
    return accounts.get(accountId);
  }

  @Override
  public void clearAccounts() {
    accounts.clear();
  }

  @Override
  public void transferAmount(BalanceTransfer balanceTransfer) throws NegativeBalanceException, AccountNotExistException {
     String accountFromId = balanceTransfer.getAccountFromId();
     String accountToId = balanceTransfer.getAccountToId();

     if(accountFromId.equalsIgnoreCase(accountToId))throw new DuplicateAccountIdException("Both Account are same");

     BigDecimal amount = balanceTransfer.getAmount();


     if(!accounts.containsKey(accountFromId)) throw new AccountNotExistException("From Account doesn't exist");
     if(!accounts.containsKey(accountToId)) throw new AccountNotExistException("To Account doesn't exist");

     Account fromAccount = accounts.get(accountFromId);
     Account toAccount = accounts.get(accountToId);

     if(fromAccount.getBalance().compareTo(amount) == -1 )throw new NegativeBalanceException("Can't do amount transfer with negative balance");

     fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
     toAccount.setBalance(toAccount.getBalance().add(amount));
  }

}
