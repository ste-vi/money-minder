import {Component} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {SearchTransactionsService} from "../../../../services/communication/search-transactions-service";
import {TransactionComponent} from "../transaction.component";
import {Transaction} from "../../../../models/transaction";
import {TransactionService} from "../../../../services/api/transaction-service";
import {FormsModule} from "@angular/forms";
import {InfiniteScrollModule} from "ngx-infinite-scroll";

@Component({
  selector: 'app-search-transactions',
  standalone: true,
  imports: [
    NgIf,
    MatIcon,
    TransactionComponent,
    NgForOf,
    FormsModule,
    InfiniteScrollModule
  ],
  templateUrl: './search-transactions.component.html',
  styleUrl: './search-transactions.component.scss'
})
export class SearchTransactionsComponent {
  protected isOpened: boolean = true;
  protected transactions: Transaction[] = [];

  protected currentPage: number = 0;
  protected itemsPerPage: number = 20;
  protected hasMoreTransactions: boolean = true;
  protected searchQuery: string = '';

  constructor(private searchTransactionsService: SearchTransactionsService,
              private transactionService: TransactionService) {
    this.searchTransactionsService.modalOpened$.subscribe(() => {
      this.showModal();
    });

    this.loadTransactions(false);
  }

  private loadTransactions(isSearch: boolean = false) {
    this.transactionService.searchTransactions(this.currentPage, this.itemsPerPage, this.searchQuery)
      .subscribe((pageResponse) => {
        this.transactions = isSearch ? pageResponse.content : this.transactions.concat(pageResponse.content);
        this.hasMoreTransactions = !pageResponse.last;
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

  private showModal() {
    this.isOpened = true;
    this.searchQuery = '';
  }

  closeModal() {
    this.isOpened = false
  }
}
