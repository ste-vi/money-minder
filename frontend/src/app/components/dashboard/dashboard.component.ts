import {Component} from '@angular/core';
import {MatIcon} from '@angular/material/icon';
import {Transaction, TransactionType} from '../../models/transaction';
import {TransactionService} from '../../services/api/transaction-service';
import {DatePipe, DecimalPipe, NgForOf, NgIf} from "@angular/common";
import {TransactionComponent} from "../common/transaction/transaction.component";
import {SearchTransactionsService} from "../../services/communication/search-transactions-service";
import {LoaderComponent} from "../common/loader/loader.component";

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [MatIcon, NgForOf, DatePipe, DecimalPipe, TransactionComponent, LoaderComponent, NgIf],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent {
  protected transactions: Transaction[] = [];
  protected isLoading: boolean = false;

  constructor(private transactionService: TransactionService,
              private searchTransactionService: SearchTransactionsService) {
    this.isLoading = true;
    this.transactionService
      .getLastTransactions(5)
      .subscribe((pageResponse) => {
        this.transactions = pageResponse.content;
        this.isLoading = false
      });
  }

  openSearchTransactionsModal() {
    this.searchTransactionService.openModal({});
  }
}
