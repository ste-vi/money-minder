import {Component, ElementRef, ViewChild} from '@angular/core';
import {DatePipe, DecimalPipe, NgClass, NgIf} from "@angular/common";
import {TransactionType} from "../../../../models/transaction";
import {AutoResizeDirective} from "../../../../directives/auto-resize.directive";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatIcon} from "@angular/material/icon";
import {CreateTransactionService} from "../../../../services/communication/create-transaction-service";
import {AccountService} from "../../../../services/api/account-service";
import {Category, CategoryType} from "../../../../models/category";
import {SelectCategoryService} from "../../../../services/communication/select-category-service";
import {
  TransactionAccountFilterComponent
} from "../search-transactions/filters/transaction-account-filter/transaction-account-filter.component";
import {Account} from "../../../../models/account";
import {TransactionService} from "../../../../services/api/transaction-service";

@Component({
  selector: 'app-create-transaction',
  standalone: true,
  imports: [
    NgIf,
    AutoResizeDirective,
    DatePipe,
    DecimalPipe,
    FormsModule,
    MatIcon,
    ReactiveFormsModule,
    NgClass,
    TransactionAccountFilterComponent
  ],
  templateUrl: './create-transaction.component.html',
  styleUrl: './create-transaction.component.scss'
})
export class CreateTransactionComponent {

  protected isOpened: boolean = false;

  isExpenseTabActive: boolean = true;
  isIncomeTabActive: boolean = false;
  isTransferTabActive: boolean = false;

  isAccountFilterOpened: boolean = false;

  // @ts-ignore
  @ViewChild('amountInput') protected amountInput: ElementRef;

  protected category: Category | undefined = undefined;
  protected transactionType: TransactionType = TransactionType.EXPENSE;

  protected transactionForm: FormGroup = new FormGroup({
    name: new FormControl('', Validators.required),
    amount: new FormControl('0.00', Validators.min(0.01)),
    account: new FormControl(Validators.required),
    date: new FormControl(new Date(), Validators.required),
    notes: new FormControl(''),
  });

  constructor(private createTransactionService: CreateTransactionService,
              private accountService: AccountService,
              private selectCategoryService: SelectCategoryService,
              private transactionService: TransactionService) {
    this.createTransactionService.modalOpened$.subscribe(() => {
      this.openModal();
    });

    this.selectCategoryService.categorySelected$.subscribe((category) => {
      this.category = category;
    });
  }

  private openModal() {
    this.accountService.getDefaultAccount().subscribe(account => {
      this.transactionForm.controls['account'].setValue(account);
    })
    this.isOpened = true;
  }

  closeModal() {
    this.isOpened = false;
  }

  selectCategory() {
    let categoryType = this.transactionType === TransactionType.EXPENSE ? CategoryType.EXPENSE : CategoryType.INCOME;
    this.selectCategoryService.openModal(categoryType);
  }

  formatAmount() {
    let value = this.transactionForm.controls['amount'].value;
    if (!value || value === 0) {
      this.transactionForm.setErrors({invalid: true});
      return;
    }

    value = parseFloat(value).toFixed(2);
    this.transactionForm.controls['amount'].setValue(value, {
      emitEvent: false,
    });

    const event = new Event('input', {
      bubbles: true,
      cancelable: true,
    });
    this.amountInput.nativeElement.dispatchEvent(event);
  }

  selectExpenseTab() {
    this.isExpenseTabActive = true;
    this.isIncomeTabActive = false;
    this.isTransferTabActive = false;

    this.category = undefined;

    this.transactionType = TransactionType.EXPENSE;
  }

  selectIncomeTab() {
    this.isIncomeTabActive = true;
    this.isExpenseTabActive = false;
    this.isTransferTabActive = false;

    this.category = undefined;

    this.transactionType = TransactionType.INCOME;
  }

  selectTransferTab() {
    this.isTransferTabActive = true;
    this.isIncomeTabActive = false;
    this.isExpenseTabActive = false;

    this.transactionType = TransactionType.INCOME;
  }

  openAccountFilter() {
    this.isAccountFilterOpened = true;
  }

  closeAccountFilter() {
    this.isAccountFilterOpened = false;
  }

  onAccountSelected(account: Account) {
    this.transactionForm.controls['account'].setValue(account);
  }

  save() {
    if (!this.transactionForm.valid) {
      return;
    }

    let createRequest = {
      "fromAccountId": this.transactionForm.controls["account"].value.id,
      "currency": this.transactionForm.controls["account"].value.currency.shortName,
      "date": new Date(this.transactionForm.controls["date"].value),
      "amount": this.transactionForm.controls["amount"].value,
      "notes": this.transactionForm.controls["notes"].value,
      "name": this.transactionForm.controls["name"].value,
      "categoryId": this.category?.id,
      "type": this.transactionType
    }
    this.transactionService.create(createRequest).subscribe(transaction => {
      this.closeModal()
      window.location.reload();
      // todo: think how to update list of transactions without reloading the page
    })
  }
}
