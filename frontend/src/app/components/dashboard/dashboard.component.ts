import { Component } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { Transaction } from '../../models/transaction';
import { TransactionService } from '../../services/api/transaction-service';
import {DatePipe, DecimalPipe, NgForOf} from "@angular/common";
import {TransactionComponent} from "../common/transaction/transaction.component";

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [MatIcon, NgForOf, DatePipe, DecimalPipe, TransactionComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent {
  protected transactions: Transaction[] = [];

  constructor(private transactionService: TransactionService) {
    this.transactionService
      .getLastTransactions()
      .subscribe((transactions) => (this.transactions = transactions));
  }
}
