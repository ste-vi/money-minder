import {
  AfterViewChecked,
  Component,
  ElementRef,
  OnInit,
  ViewChild,
} from '@angular/core';
import {DatePipe, DecimalPipe, NgForOf, NgIf} from '@angular/common';
import {AutoResizeDirective} from '../../../../directives/auto-resize.directive';
import {
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import {MatIcon} from '@angular/material/icon';
import {Transaction, TransactionType} from '../../../../models/transaction';
import {ViewTransactionService} from '../../../../services/communication/view-transaction-service';
import {MatFormField, MatSuffix} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatDatepicker, MatDatepickerInput, MatDatepickerToggle} from "@angular/material/datepicker";
import {TransactionService} from "../../../../services/api/transaction-service";

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
  ],
  templateUrl: './transaction-view.component.html',
  styleUrl: './transaction-view.component.scss',
})
export class TransactionViewComponent implements OnInit, AfterViewChecked {
  protected isOpened: boolean = false;
  protected editName: boolean = false;

  protected transaction!: Transaction;

  protected transactionForm: FormGroup = new FormGroup({
    name: new FormControl('', Validators.required),
    amount: new FormControl(Validators.required),
    account: new FormControl(Validators.required),
    date: new FormControl(Validators.required),
    notes: new FormControl(''),
  });

  // @ts-ignore
  @ViewChild('amountInput') protected amountInput: ElementRef;
  // @ts-ignore
  @ViewChild('nameInput') nameInputRef: ElementRef;

  constructor(private viewTransactionService: ViewTransactionService,
              private transactionService: TransactionService) {
    this.transactionForm = new FormGroup({
      name: new FormControl('', Validators.required),
      amount: new FormControl(Validators.required),
      account: new FormControl(Validators.required),
      date: new FormControl(Validators.required),
      notes: new FormControl(''),
    });
  }

  ngOnInit(): void {
    this.viewTransactionService.modalOpened$.subscribe((transaction) => {
      this.transaction = transaction;

      this.transactionForm.controls['name'].setValue(transaction.name);
      this.transactionForm.controls['date'].setValue(transaction.date);
      this.transactionForm.controls['account'].setValue(transaction.fromAccount);
      this.transactionForm.controls['notes'].setValue(transaction.notes);
      this.transactionForm.controls['amount'].setValue(transaction.amount);

      if (transaction.isBankTransaction) {
        this.transactionForm.controls['amount'].disable();
      } else {
        this.transactionForm.controls['amount'].enable();
      }

      this.showModal();
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
    this.transactionForm.reset()
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

  inputTransactionNameBlue() {
    this.editName = false;
  }

  save() {
    let amount: number = this.transaction.type === TransactionType.EXPENSE
      ? this.transactionForm.controls["amount"].value * 1
      : this.transactionForm.controls["amount"].value;

    let updateRequest = {
      "name": this.transactionForm.controls["name"].value,
      "amount": amount,
      "date": new Date(this.transactionForm.controls["date"].value),
      "notes": this.transactionForm.controls["notes"].value,
      "categoryId": "4c8e867b-2ffe-477b-8edd-402e8cf8a167"
    }
    this.transactionService.update(this.transaction.id, updateRequest).subscribe(data => {
      this.transaction.name = this.transactionForm.controls["name"].value;
      this.transaction.amount = amount;
      this.transaction.date = this.transactionForm.controls["date"].value;
      this.transaction.notes = this.transactionForm.controls["notes"].value;
      this.closeModal()
    })
  }

  delete() {
    this.transactionService.delete(this.transaction.id).subscribe(data => {
      this.closeModal();
      window.location.reload();
    })
  }

  // todo: add category select modal
  // todo: implement form validation logic
}
