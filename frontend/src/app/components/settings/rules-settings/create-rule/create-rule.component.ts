import {Component} from '@angular/core';
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {CreateRuleService} from "../../../../services/communication/create-rule-service";
import {RuleService} from "../../../../services/api/rule-service";
import {ConditionTypeEnum} from "../../../../models/rule";
import {Category, CategoryType} from "../../../../models/category";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {SelectCategoryService} from "../../../../services/communication/select-category-service";

@Component({
  selector: 'app-create-rule',
  standalone: true,
  imports: [
    NgIf,
    MatIcon,
    NgClass,
    NgForOf,
    ReactiveFormsModule,
    FormsModule
  ],
  templateUrl: './create-rule.component.html',
  styleUrl: './create-rule.component.scss'
})
export class CreateRuleComponent {

  protected isOpened: boolean = false;

  protected readonly ConditionTypeEnum = ConditionTypeEnum;

  protected conditionType: ConditionTypeEnum = ConditionTypeEnum.TEXT_CONTAINS;
  protected conditionText: string = "";
  protected assignCategory: Category | undefined = undefined;
  protected applyToExistingTransactions: boolean = false;

  constructor(private createRuleService: CreateRuleService,
              private ruleService: RuleService,
              private selectCategoryService: SelectCategoryService) {
    this.createRuleService.modalOpened$.subscribe(() => {
      this.openModal();
    });

    this.selectCategoryService.categorySelected$.subscribe((category) => {
      this.assignCategory = category;
    });
  }

  private openModal() {
    this.isOpened = true;
  }

  closeModal() {
    this.isOpened = false;
  }

  selectCategory() {
    this.selectCategoryService.openModal(CategoryType.EXPENSE)
  }

  selectConditionType(type: ConditionTypeEnum) {
    this.conditionType = type;
  }

  createRule() {
    if (this.conditionText == "" || this.assignCategory == undefined) {
      return;
    }

    let request = {
      "assignCategoryId": this.assignCategory?.id,
      "conditionText": this.conditionText,
      "conditionType": this.conditionType
    }

    this.ruleService.createRule(request, this.applyToExistingTransactions).subscribe(() => {
      this.closeModal();
    })
  }

}
