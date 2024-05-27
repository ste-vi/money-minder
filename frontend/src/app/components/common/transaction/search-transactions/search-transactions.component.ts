import {Component, ElementRef, EventEmitter, Output, ViewChild} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {SearchTransactionsService} from "../../../../services/communication/search-transactions-service";
import {TransactionComponent} from "../transaction.component";
import {Transaction} from "../../../../models/transaction";
import {TransactionService} from "../../../../services/api/transaction-service";
import {debounceTime, distinctUntilChanged, Subject, switchMap} from "rxjs";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-search-transactions',
  standalone: true,
  imports: [
    NgIf,
    MatIcon,
    TransactionComponent,
    NgForOf,
    FormsModule
  ],
  templateUrl: './search-transactions.component.html',
  styleUrl: './search-transactions.component.scss'
})
export class SearchTransactionsComponent {
  protected isOpened: boolean = true;
  protected transactions: Transaction[] = [];

  currentPage: number = 0;
  itemsPerPage: number = 20;
  hasMoreTransactions: boolean = true;

  @ViewChild('scrollContainer', {static: true}) scrollContainer: ElementRef | undefined;
  private searchSubject = new Subject<string>();

  protected searchQuery: string = '';

  constructor(private searchTransactionsService: SearchTransactionsService,
              private transactionService: TransactionService) {
    this.searchTransactionsService.modalOpened$.subscribe((isOpened) => {
      this.showModal();
    });

    this.loadTransactions();
    this.setSearchEventListener();

    this.searchSubject.next('');
  }

  private loadTransactions() {
    this.transactionService.searchTransactions(this.currentPage, this.itemsPerPage, this.searchQuery)
      .subscribe((pageResponse) => {
        this.transactions = this.transactions.concat(pageResponse.content);
        this.hasMoreTransactions = !pageResponse.last;
      })
  }

  private setSearchEventListener(): void {
    this.searchSubject
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap((query) =>
          this.transactionService.searchTransactions(this.currentPage, this.itemsPerPage, this.searchQuery)
        )
      )
      .subscribe((pageResponse) => {
        this.transactions = pageResponse.content
        this.hasMoreTransactions = !pageResponse.last;
      });
  }

  onScroll(): void {
    console.log("test");
    if (this.scrollContainer && this.hasMoreTransactions) {
      const scrollPosition = this.scrollContainer.nativeElement.scrollTop + this.scrollContainer.nativeElement.clientHeight;

      const scrollHeight = this.scrollContainer.nativeElement.scrollHeight;
      if (scrollPosition >= scrollHeight) {
        this.currentPage++;
        this.loadTransactions();
      }
    }
  }

  search(): void {
    this.searchSubject.next(this.searchQuery);
    this.currentPage = 0;
  }

  private showModal() {
    this.isOpened = true;
  }

  closeModal() {
    this.isOpened = false
  }
}
