import {Component} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {SearchTransactionsService} from "../../../../services/communication/search-transactions-service";
import {TransactionComponent} from "../transaction.component";
import {Transaction} from "../../../../models/transaction";
import {TransactionService} from "../../../../services/api/transaction-service";
import {FormsModule} from "@angular/forms";
import {InfiniteScrollModule} from "ngx-infinite-scroll";
import {LoaderComponent} from "../../loader/loader.component";
import {SearchTransactionFilters} from "./model/search-transaction-filters";

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
  ],
  templateUrl: './search-transactions.component.html',
  styleUrl: './search-transactions.component.scss'
})
export class SearchTransactionsComponent {
  protected isOpened: boolean = false;
  protected transactions: Transaction[] = [];

  protected currentPage: number = 0;
  protected itemsPerPage: number = 20;
  protected hasMoreTransactions: boolean = true;
  protected searchQuery: string = '';
  protected accountId: string | undefined = undefined;
  protected isLoading: boolean = true;

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

    this.transactionService.searchTransactions(this.currentPage, this.itemsPerPage, this.searchQuery, this.accountId)
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

  private showModal(filters: SearchTransactionFilters) {
    this.accountId = filters.accountId
    this.searchQuery = '';
    this.isOpened = true;
    this.loadTransactions(true);
  }

  closeModal() {
    this.isOpened = false
  }
}
