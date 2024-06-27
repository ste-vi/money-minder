import { Component, OnInit } from '@angular/core';
import { DatePipe, DecimalPipe, NgForOf, NgIf } from '@angular/common';
import { NgApexchartsModule } from 'ng-apexcharts';
import { CategoryService } from '../../../../services/api/category-service';
import { TopExpense } from '../../../../models/top-expense';
import { MatIcon } from '@angular/material/icon';
import { ViewCategoryExpensesService } from '../../../../services/communication/view-category-expenses-service';
import {
  ViewSearchCategoryExpensesService
} from "../../../../services/communication/view-search-category-expenses-service";

export type ChartOptions = {
  chart: any | undefined;
  dataLabels: any | undefined;
  plotOptions: any | undefined;
  legend: any | undefined;
};

@Component({
  selector: 'app-top-expenses-widget',
  standalone: true,
  imports: [DatePipe, NgForOf, NgApexchartsModule, MatIcon, DecimalPipe, NgIf],
  templateUrl: './top-expenses-widget.component.html',
  styleUrls: ['./top-expenses-widget.component.scss'],
})
export class TopExpensesWidgetComponent implements OnInit {
  protected readonly currentDate: Date = new Date();

  protected topExpenses: TopExpense[] = [];
  protected moreExpense: TopExpense | undefined = undefined;
  protected totalExpenses: number = 0;
  protected totalExpenseCurrencySign: string = '$';

  protected dateFrom: Date;
  protected dateTo: Date;

  constructor(
    private categoryService: CategoryService,
    private viewCategoryExpensesService: ViewCategoryExpensesService,
    private viewSearchCategoryExpensesService: ViewSearchCategoryExpensesService
  ) {
    const date = new Date();
    this.dateFrom = new Date(date.getFullYear(), date.getMonth(), 2);
    this.dateTo = new Date(date.getFullYear(), date.getMonth() + 1, 1);
  }

  ngOnInit(): void {
    this.loadTopExpenses();
    this.categoryService.refreshTopExpenses$.subscribe(() => {
      this.loadTopExpenses();
    });
  }

  private loadTopExpenses() {
    this.categoryService
      .getTopExpensesByCategories(this.dateFrom, this.dateTo)
      .subscribe((topExpenses) => {
        this.topExpenses = topExpenses;
        this.totalExpenses = this.topExpenses.reduce(
          (total, expense) => total + expense.total,
          0,
        );
        this.topExpenses = topExpenses.map((expense) => {
          const percentage = (expense.total / this.totalExpenses) * 100;
          return { ...expense, percentage };
        });
        this.totalExpenseCurrencySign = this.topExpenses[0]?.currencySign;

        if (this.topExpenses.length > 12) {
          const remainingExpenses = this.topExpenses.slice(12);
          const remainingTotal = remainingExpenses.reduce(
            (total, expense) => total + expense.total,
            0,
          );
          const remainingPercentage =
            (remainingTotal / this.totalExpenses) * 100;

          this.moreExpense = {
            category: undefined,
            total: remainingTotal,
            percentage: remainingPercentage,
            currencySign: this.totalExpenseCurrencySign,
          };
        }
      });
  }

  openCategoryExpensesPage(topExpense: TopExpense) {
    this.viewCategoryExpensesService.openModal({
      topExpense: topExpense,
      dateFrom: this.dateFrom,
      dateTo: this.dateTo,
    });
  }

  openSearchTopExpenses() {
    this.viewSearchCategoryExpensesService.openModal()
  }
}
