import {
  AfterViewChecked,
  Component,
  ElementRef,
  OnInit,
  ViewChild,
} from '@angular/core';
import { DatePipe, DecimalPipe, NgClass, NgForOf, NgIf } from '@angular/common';
import { AutoResizeDirective } from '../../../../directives/auto-resize.directive';
import {
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatIcon } from '@angular/material/icon';
import { Transaction, TransactionType } from '../../../../models/transaction';
import { ViewTransactionService } from '../../../../services/communication/view-transaction-service';
import { MatFormField, MatSuffix } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import {
  MatDatepicker,
  MatDatepickerInput,
  MatDatepickerToggle,
} from '@angular/material/datepicker';
import { TransactionService } from '../../../../services/api/transaction-service';
import { CategoriesComponent } from '../../categories/categories.component';
import { CategoryType } from '../../../../models/category';
import { SelectCategoryService } from '../../../../services/communication/select-category-service';
import { TransactionAccountFilterComponent } from '../search-transactions/filters/transaction-account-filter/transaction-account-filter.component';
import { Account } from '../../../../models/account';

@Component({
  selector: 'app-transaction-view',
  standalone: true,
  imports: [
    NgIf,
    AutoResizeDirective,
    FormsModule,
    MatIcon,
    NgForOf,
    ReactiveFormsModule,
    DatePipe,
    DecimalPipe,
    MatFormField,
    MatInput,
    MatDatepickerInput,
    MatDatepickerToggle,
    MatDatepicker,
    MatSuffix,
    CategoriesComponent,
    NgClass,
    TransactionAccountFilterComponent,
  ],
  templateUrl: './transaction-view.component.html',
  styleUrl: './transaction-view.component.scss',
})
export class TransactionViewComponent implements OnInit, AfterViewChecked {
  protected isOpened: boolean = false;
  protected editName: boolean = false;
  protected isToAccountFilterOpened: boolean = false;

  protected transaction!: Transaction;

  protected readonly TransactionType = TransactionType;

  protected transactionForm: FormGroup = new FormGroup({
    name: new FormControl('', Validators.required),
    amount: new FormControl(Validators.required),
    fromAccount: new FormControl(Validators.required),
    toAccount: new FormControl(Validators.required),
    date: new FormControl(Validators.required),
    notes: new FormControl(''),
  });

  // @ts-ignore
  @ViewChild('amountInput') protected amountInput: ElementRef;
  // @ts-ignore
  @ViewChild('nameInput') nameInputRef: ElementRef;

  constructor(
    private viewTransactionService: ViewTransactionService,
    private transactionService: TransactionService,
    private selectCategoryService: SelectCategoryService,
  ) {
    this.transactionForm = new FormGroup({
      name: new FormControl('', Validators.required),
      amount: new FormControl(Validators.required),
      fromAccount: new FormControl(Validators.required),
      toAccount: new FormControl(),
      date: new FormControl(Validators.required),
      notes: new FormControl(''),
    });
  }

  ngOnInit(): void {
    this.viewTransactionService.modalOpened$.subscribe((transaction) => {
      this.transaction = transaction;

      this.transactionForm.controls['name'].setValue(transaction.name);
      this.transactionForm.controls['date'].setValue(transaction.date);
      this.transactionForm.controls['fromAccount'].setValue(
        transaction.fromAccount,
      );
      this.transactionForm.controls['toAccount'].setValue(
        transaction.toAccount,
      );
      this.transactionForm.controls['notes'].setValue(transaction.notes);
      this.transactionForm.controls['amount'].setValue(transaction.amount);

      if (transaction.isBankTransaction) {
        this.transactionForm.controls['amount'].disable();
      } else {
        this.transactionForm.controls['amount'].enable();
      }

      this.showModal();
    });

    this.selectCategoryService.categorySelected$.subscribe((category) => {
      this.transaction.category = category;
    });
  }

  ngAfterViewChecked() {
    if (this.editName) {
      this.nameInputRef.nativeElement.focus();
    }
  }

  showModal() {
    this.isOpened = true;
    setTimeout(() => {
      this.formatAmount();
    }, 100);
  }

  closeModal() {
    this.isOpened = false;
    this.editName = false;
    this.transactionForm.reset();
  }

  formatAmount() {
    let value = this.transactionForm.controls['amount'].value;
    if (!value || value === 0) {
      this.transactionForm.setErrors({ invalid: true });
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

  inputTransactionNameBlur() {
    this.editName = false;
  }

  save() {
    if (
      this.transactionForm.controls['fromAccount'].value.id ===
      this.transactionForm.controls['toAccount'].value?.id
    ) {
      return;
    }

    let updateRequest = {
      name: this.transactionForm.controls['name'].value,
      amount: this.transactionForm.controls['amount'].value,
      toAccountId: this.transactionForm.controls['toAccount']?.value?.id,
      date: new Date(this.transactionForm.controls['date'].value),
      notes: this.transactionForm.controls['notes'].value,
      categoryId: this.transaction.category?.id,
    };
    this.transactionService
      .update(this.transaction.id, updateRequest)
      .subscribe((data) => {
        this.transaction.name = this.transactionForm.controls['name'].value;
        this.transaction.amount = this.transactionForm.controls['amount'].value;
        this.transaction.toAccount = this.transactionForm.controls['toAccount']?.value;
        this.transaction.date = this.transactionForm.controls['date'].value;
        this.transaction.notes = this.transactionForm.controls['notes'].value;
        this.closeModal();
      });
  }

  delete() {
    this.transactionService.delete(this.transaction).subscribe((data) => {
      this.closeModal();
    });
  }

  selectCategory() {
    let categoryType =
      this.transaction.type === TransactionType.EXPENSE
        ? CategoryType.EXPENSE
        : CategoryType.INCOME;
    this.selectCategoryService.openModal(categoryType);
  }

  openToAccountFilter() {
    this.isToAccountFilterOpened = true;
  }

  closeToAccountFilter() {
    this.isToAccountFilterOpened = false;
  }

  onToAccountSelected(account: Account) {
    this.transactionForm.controls['toAccount'].setValue(account);
  }
}
