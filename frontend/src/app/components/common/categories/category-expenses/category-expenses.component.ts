import { Component } from '@angular/core';
import { CreateTransactionButtonComponent } from '../../transaction/create-transaction-button/create-transaction-button.component';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { LoaderComponent } from '../../loader/loader.component';
import { MatIcon } from '@angular/material/icon';
import { NgForOf, NgIf } from '@angular/common';
import { TransactionComponent } from '../../transaction/transaction.component';
import { Transaction } from '../../../../models/transaction';
import { SearchTransactionsService } from '../../../../services/communication/search-transactions-service';
import { TransactionService } from '../../../../services/api/transaction-service';
import { ViewCategoryExpensesService } from '../../../../services/communication/view-category-expenses-service';
import { TopExpense } from '../../../../models/top-expense';

@Component({
  selector: 'app-category-expenses',
  standalone: true,
  imports: [
    CreateTransactionButtonComponent,
    InfiniteScrollModule,
    LoaderComponent,
    MatIcon,
    NgForOf,
    NgIf,
    TransactionComponent,
  ],
  templateUrl: './category-expenses.component.html',
  styleUrl: './category-expenses.component.scss',
})
export class CategoryExpensesComponent {
  protected isOpen: boolean = false;

  protected transactions: Transaction[] = [];
  protected currentPage: number = 0;
  protected itemsPerPage: number = 20;
  protected hasMoreTransactions: boolean = true;
  protected searchQuery: string = '';
  protected isLoading: boolean = true;
  protected topExpense: TopExpense | undefined;
  protected dateFrom: Date | undefined = undefined;
  protected dateTo: Date | undefined = undefined;

  constructor(
    private viewCategoryExpensesService: ViewCategoryExpensesService,
    private searchTransactionsService: SearchTransactionsService,
    private transactionService: TransactionService,
  ) {
    this.viewCategoryExpensesService.modalOpened$.subscribe((object) => {
      this.topExpense = object.topExpense;
      this.dateFrom = object.dateFrom;
      this.dateTo = object.dateTo;

      this.openModal();
      this.loadTransactions();

      this.transactionService.refreshTransactions$.subscribe(() => {
        this.transactions = [];
        this.loadTransactions();
      });
    });
  }

  private loadTransactions() {
    this.isLoading = true;
    this.transactionService
      .searchTransactions(
        this.currentPage,
        this.itemsPerPage,
        this.searchQuery,
        undefined,
        this.topExpense?.category.id,
        false,
        this.dateFrom,
        this.dateTo,
      )
      .subscribe((pageResponse) => {
        this.transactions = this.transactions.concat(
          pageResponse.content,
        );
        this.hasMoreTransactions = !pageResponse.last;
        this.isLoading = false;
      });
  }

  private openModal() {
    this.transactions = [];
    this.isOpen = true;
  }

  closeModal() {
    this.isOpen = false;
  }

  openSearch() {
    this.searchTransactionsService.openModal({
      category: this.topExpense?.category,
    });
  }

  loadMore() {
    if (this.hasMoreTransactions) {
      this.currentPage++;
      this.loadTransactions();
    }
  }
}
