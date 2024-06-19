import { Component } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { NgForOf, NgIf } from '@angular/common';
import { Category } from '../../../models/category';
import { CategoryService } from '../../../services/api/category-service';
import { SelectCategoryService } from '../../../services/communication/select-category-service';

@Component({
  selector: 'app-categories',
  standalone: true,
  imports: [MatIcon, NgIf, NgForOf],
  templateUrl: './categories.component.html',
  styleUrl: './categories.component.scss',
})
export class CategoriesComponent {
  protected isOpened: boolean = false;
  protected isSubCategoriesOpened: boolean = false;

  protected categories: Category[] = [];
  protected parentCategory: Category | undefined = undefined;

  constructor(
    private categoryService: CategoryService,
    private selectCategoryService: SelectCategoryService,
  ) {
    this.selectCategoryService.modalOpened$.subscribe((type) => {
      this.openModal();
      this.categoryService.getCategories(type).subscribe((categories) => {
        this.categories = categories;
      });
    });
  }

  openModal() {
    this.isOpened = true;
  }

  closeModal() {
    this.isOpened = false;
    this.closeSubCategoriesModal();
  }

  closeSubCategoriesModal() {
    this.isSubCategoriesOpened = false;
  }

  selectCategory(category: Category) {
    if (category.subCategories && category.subCategories.length > 0) {
      this.parentCategory = category;
      this.isSubCategoriesOpened = true;
    } else {
      this.selectCategoryService.selectCategory(category);
      this.closeModal();
    }
  }

  selectSubCategory(subCategory: Category) {
    this.selectCategoryService.selectCategory(subCategory);
    this.closeModal();
  }
}
