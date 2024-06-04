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
  protected account: Account | undefined = undefined;
  protected isAccountFilterOpened: boolean = false;

  constructor(private searchTransactionsService: SearchTransactionsService,
              private transactionService: TransactionService) {

    this.searchTransactionsService.modalOpened$.subscribe((filters) => {
      this.showModal(filters);
    });
  }

  private loadTransactions(isSearch: boolean = false) {
    this.isLoading = true;
    if (isSearch) {
      this.transactions = [];
    }

    this.transactionService.searchTransactions(this.currentPage, this.itemsPerPage, this.searchQuery, this.account?.id)
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
    this.account = account;
    this.loadTransactions(true)
    this.isAccountFilterOpened = false;
  }

  private showModal(filters: SearchTransactionFilters) {
    this.account = filters.account
    this.searchQuery = '';
    this.isOpened = true;
    this.loadTransactions(true);
  }

  closeModal() {
    this.isOpened = false
  }
}
