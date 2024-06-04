import {Component} from '@angular/core';
import {ViewAccountService} from "../../../services/communication/view-account-service";
import {Account} from "../../../models/account";
import {MatIcon} from "@angular/material/icon";
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {SearchTransactionsService} from "../../../services/communication/search-transactions-service";
import {InfiniteScrollModule} from "ngx-infinite-scroll";
import {LoaderComponent} from "../../common/loader/loader.component";
import {TransactionComponent} from "../../common/transaction/transaction.component";
import {Transaction} from "../../../models/transaction";
import {TransactionService} from "../../../services/api/transaction-service";

@Component({
  selector: 'app-account-view',
  standalone: true,
  imports: [
    MatIcon,
    NgIf,
    NgClass,
    InfiniteScrollModule,
    LoaderComponent,
    NgForOf,
    TransactionComponent
  ],
  templateUrl: './account-view.component.html',
  styleUrl: './account-view.component.scss'
})
export class AccountViewComponent {

  protected isOpen: boolean = false;
  protected account: Account | undefined;

  protected accountTransactions: Transaction[] = [];
  protected currentPage: number = 0;
  protected itemsPerPage: number = 20;
  protected hasMoreTransactions: boolean = true;
  protected searchQuery: string = '';
  protected isLoading: boolean = true;

  constructor(private viewAccountService: ViewAccountService,
              private searchTransactionsService: SearchTransactionsService,
              private transactionService: TransactionService) {
    this.viewAccountService.modalOpened$.subscribe((account) => {
      this.account = account;
      this.openModal();

      this.loadTransactions();
    });
  }

  private loadTransactions() {
    this.isLoading = true;
    this.transactionService.searchTransactions(this.currentPage, this.itemsPerPage, this.searchQuery, this.account?.id)
      .subscribe((pageResponse) => {
        this.accountTransactions = this.accountTransactions.concat(pageResponse.content);
        this.hasMoreTransactions = !pageResponse.last;
        this.isLoading = false;
      })
  }

  private openModal() {
    this.accountTransactions = [];
    this.isOpen = true;
  }

  closeModal() {
    this.isOpen = false;
  }

  openSearch() {
    this.searchTransactionsService.openModal({account: this.account});
  }

  loadMore() {
    if (this.hasMoreTransactions) {
      this.currentPage++;
      this.loadTransactions();
    }
  }

  editModal() {
    // implement
  }
}
