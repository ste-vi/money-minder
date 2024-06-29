import { Component } from '@angular/core';
import { ViewSearchCategoryExpensesService } from '../../../../services/communication/view-search-category-expenses-service';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { LoaderComponent } from '../../loader/loader.component';
import { MatIcon } from '@angular/material/icon';
import { DatePipe, DecimalPipe, NgForOf, NgIf } from '@angular/common';
import { TransactionComponent } from '../../transaction/transaction.component';
import { TopExpense } from '../../../../models/top-expense';
import { CategoryService } from '../../../../services/api/category-service';
import { ViewCategoryExpensesService } from '../../../../services/communication/view-category-expenses-service';
import { CategoryType } from '../../../../models/category';

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
    DecimalPipe,
    DatePipe,
  ],
  templateUrl: './search-category-expenses.component.html',
  styleUrl: './search-category-expenses.component.scss',
})
export class SearchCategoryExpensesComponent {
  protected isOpen: boolean = true;

  protected topExpenses: TopExpense[] = [];
  protected totalExpensesAmount: number = 0;
  protected totalExpenseCurrencySign: string = '$';

  protected dateFrom: Date;
  protected dateTo: Date;
  protected readonly currentDate: Date = new Date();

  protected readonly CategoryType = CategoryType;
  protected categoryType: CategoryType = CategoryType.EXPENSE;
  protected isLoading: boolean = false;

  constructor(
    private viewSearchCategoryExpensesService: ViewSearchCategoryExpensesService,
    private viewCategoryExpensesService: ViewCategoryExpensesService,
    private categoryService: CategoryService,
  ) {
    const date = new Date();
    this.dateFrom = new Date(date.getFullYear(), date.getMonth(), 2);
    this.dateTo = new Date(date.getFullYear(), date.getMonth() + 1, 1);

    this.viewSearchCategoryExpensesService.modalOpened$.subscribe(() => {
      this.loadTopExpenses();
      this.isOpen = true;
    });

    this.loadTopExpenses(); //remove
  }

  private loadTopExpenses() {
    this.isLoading = true;
    this.categoryService
      .getTopExpensesByCategories(this.dateFrom, this.dateTo, this.categoryType)
      .subscribe((topExpenses) => {
        this.topExpenses = topExpenses;

        this.totalExpenseCurrencySign = topExpenses[0]?.currencySign;

        this.calculateTotalExpensesAmount(topExpenses);
        this.calculatePercentages(topExpenses);

        this.isLoading = false;
      });
  }

  private calculateTotalExpensesAmount(topExpenses: TopExpense[]) {
    this.totalExpensesAmount = topExpenses.reduce(
      (total, expense) => total + expense.total,
      0,
    );
  }

  private calculatePercentages(topExpenses: TopExpense[]) {
    topExpenses.forEach((expense) => {
      expense.percentage = (expense.total / this.totalExpensesAmount) * 100;
    });
  }

  closeModal() {
    this.isOpen = false;
  }

  openCategoryTransactionsView(topExpense: TopExpense) {
    this.viewCategoryExpensesService.openModal({
      topExpense: topExpense,
      dateFrom: this.dateFrom,
      dateTo: this.dateTo,
    });
  }

  changeCategoriesType() {
    this.categoryType =
      this.categoryType === CategoryType.EXPENSE
        ? CategoryType.INCOME
        : CategoryType.EXPENSE;
    this.loadTopExpenses();
  }
}
