import {Component} from '@angular/core';
import {CategorySettingsService} from '../../../services/communication/category-settings-service';
import {NgForOf, NgIf} from '@angular/common';
import {MatIcon} from '@angular/material/icon';
import {CategoryService} from "../../../services/api/category-service";
import {Category, CategoryType} from "../../../models/category";
import {MatTab, MatTabGroup} from "@angular/material/tabs";

@Component({
  selector: 'app-category-settings',
  standalone: true,
  imports: [NgIf, MatIcon, NgForOf, MatTabGroup, MatTab],
  templateUrl: './category-settings.component.html',
  styleUrl: './category-settings.component.scss',
})
export class CategorySettingsComponent {
  protected isOpened: boolean = true;

  protected expenseCategories: Category[] = [];
  protected incomeCategories: Category[] = [];

  protected isExpenseTabActive: boolean = true;
  protected isIncomeTabActive: boolean = false;

  constructor(private categorySettingsService: CategorySettingsService, private categoryService: CategoryService) {
    this.categorySettingsService.modalOpened$.subscribe((isOpened) => {
      this.showModal();
    });
    this.categoryService.getCategories().subscribe((categories) => {
      this.expenseCategories = categories.filter(category => category.type === CategoryType.EXPENSE);
      this.incomeCategories = categories.filter(category => category.type === CategoryType.INCOME);
    });
  }

  private showModal() {
    this.isOpened = true;
  }

  closeModal() {
    this.isOpened = false;
  }

  addCategory() {
  }

  viewCategory(category: Category) {

  }

  viewSubCategory(subCategory: Category) {

  }

  tabs = [
    {label: 'Expense', active: true},
    {label: 'Income', active: false}
  ];

  selectTab(tab: any) {
    this.tabs.forEach(t => t.active = false);
    tab.active = true;
  }

  selectExpenseTab() {
    this.isExpenseTabActive = true;
    this.isIncomeTabActive = false;
  }

  selectIncomeTab() {
    this.isIncomeTabActive = true;
    this.isExpenseTabActive = false;
  }
}
