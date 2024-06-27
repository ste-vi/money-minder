import { Component } from '@angular/core';
import {
  ViewSearchCategoryExpensesService
} from "../../../../services/communication/view-search-category-expenses-service";
import {InfiniteScrollModule} from "ngx-infinite-scroll";
import {LoaderComponent} from "../../loader/loader.component";
import {MatIcon} from "@angular/material/icon";
import {NgForOf, NgIf} from "@angular/common";
import {TransactionComponent} from "../../transaction/transaction.component";

@Component({
  selector: 'app-search-category-expenses',
  standalone: true,
  imports: [
    InfiniteScrollModule,
    LoaderComponent,
    MatIcon,
    NgForOf,
    NgIf,
    TransactionComponent,
  ],
  templateUrl: './search-category-expenses.component.html',
  styleUrl: './search-category-expenses.component.scss',
})
export class SearchCategoryExpensesComponent {
  protected isOpen: boolean = false;

  constructor(
    private viewSearchCategoryExpensesService: ViewSearchCategoryExpensesService,
  ) {
    this.viewSearchCategoryExpensesService.modalOpened$.subscribe(() => {
      this.isOpen = true;
    });
  }

  closeModal() {
    this.isOpen = false;
  }
}
