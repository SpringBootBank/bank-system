package com.itgirls.bank_system.service;


import com.itgirls.bank_system.dto.TransactionDto;
import com.itgirls.bank_system.model.Transactions;

import java.util.List;

public interface TransactionService {

    List<TransactionDto> findByBankUser_Id(Long userId);

    List<TransactionDto> findAllTransactions();

    Transactions updateAmountOfTransaction(TransactionDto transactionDto);
}
