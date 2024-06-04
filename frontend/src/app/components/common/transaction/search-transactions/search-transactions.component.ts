import {Component} from '@angular/core';
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {SearchTransactionsService} from "../../../../services/communication/search-transactions-service";
import {TransactionComponent} from "../transaction.component";
import {Transaction} from "../../../../models/transaction";
import {TransactionService} from "../../../../services/api/transaction-service";
import {FormsModule} from "@angular/forms";
import {InfiniteScrollModule} from "ngx-infinite-scroll";
import {LoaderComponent} from "../../loader/loader.component";
import {SearchTransactionFilters} from "./model/search-transaction-filters";
import {Account} from "../../../../models/account";
import {
  TransactionAccountFilterComponent
} from "./filters/transaction-account-filter/transaction-account-filter.component";

@Component({
  selector: 'app-search-transactions',
  standalone: true,
  imports: [
    NgIf,
    MatIcon,
    TransactionComponent,
    NgForOf,
    FormsModule,
    InfiniteScrollModule,
    LoaderComponent,
    NgClass,
    TransactionAccountFilterComponent,
  ],
  templateUrl: './search-transactions.component.html',
  styleUrl: './search-transactions.component.scss'
})
export class SearchTransactionsComponent {

  protected isOpened: boolean = false;

  protected transactions: Transaction[] = [];

  // pagination
  protected currentPage: number = 0;
  protected itemsPerPage: number = 20;
  protected hasMoreTransactions: boolean = true;
  protected isLoading: boolean = true;

  // search
  protected searchQuery: string = '';

  // filters
  protected accountFilter: Account | undefined = undefined;
  protected isAccountFilterOpened: boolean = false;

  protected needReviewFilter: boolean = false;

  constructor(private searchTransactionsService: SearchTransactionsService,
              private transactionService: TransactionService) {

    this.searchTransactionsService.modalOpened$.subscribe((filters) => {
      this.showModal(filters);
    });
  }

  private showModal(filters: SearchTransactionFilters) {
    this.accountFilter = filters.account
    this.searchQuery = '';
    this.isOpened = true;
    this.loadTransactions(true);
  }

  closeModal() {
    this.isOpened = false
  }

  private loadTransactions(isSearch: boolean = false) {
    this.isLoading = true;
    if (isSearch) {
      this.transactions = [];
    }

    this.transactionService
      .searchTransactions(
        this.currentPage,
        this.itemsPerPage,
        this.searchQuery,
        this.accountFilter?.id,
        this.needReviewFilter)
      .subscribe((pageResponse) => {
        this.transactions = isSearch ? pageResponse.content : this.transactions.concat(pageResponse.content);
        this.hasMoreTransactions = !pageResponse.last;
        this.isLoading = false;
      })
  }

  search(): void {
    this.loadTransactions(true)
    this.currentPage = 0;
  }

  loadMore() {
    if (this.hasMoreTransactions) {
      this.currentPage++;
      this.loadTransactions(false);
    }
  }

  openAccountFilter() {
    this.isAccountFilterOpened = true;
  }

  closeAccountFilter() {
    this.isAccountFilterOpened = false;
  }

  onAccountSelected(account: Account) {
    this.accountFilter = account;
    this.currentPage = 0
    this.isAccountFilterOpened = false;
    this.loadTransactions(true)
  }

  applyNeedReviewFilter() {
    this.needReviewFilter = !this.needReviewFilter;
    this.currentPage = 0
    this.loadTransactions(true);
  }

  resetFilters() {
    this.accountFilter = undefined;
    this.needReviewFilter = false
    this.searchQuery = '';
    this.loadTransactions(true);
  }
}
