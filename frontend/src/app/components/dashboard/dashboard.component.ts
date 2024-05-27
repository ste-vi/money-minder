import {Component} from '@angular/core';
import {MatIcon} from '@angular/material/icon';
import {Transaction, TransactionType} from '../../models/transaction';
import {TransactionService} from '../../services/api/transaction-service';
import {DatePipe, DecimalPipe, NgForOf} from "@angular/common";
import {TransactionComponent} from "../common/transaction/transaction.component";
import {SearchTransactionsService} from "../../services/communication/search-transactions-service";

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [MatIcon, NgForOf, DatePipe, DecimalPipe, TransactionComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent {
  protected transactions: Transaction[] = [];

  constructor(private transactionService: TransactionService,
              private searchTransactionService: SearchTransactionsService) {
    this.transactionService
      .getLastTransactions(5)
      .subscribe((pageResponse) => {
        this.transactions = pageResponse.content;
      });
  }

  openSearchTransactionsModal() {
    this.searchTransactionService.openModal();
  }
}
