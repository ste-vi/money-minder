import {
  AfterViewChecked,
  Component,
  ElementRef,
  OnInit,
  ViewChild,
} from '@angular/core';
import { DatePipe, DecimalPipe, NgForOf, NgIf } from '@angular/common';
import { AutoResizeDirective } from '../../../../directives/auto-resize.directive';
import {
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatIcon } from '@angular/material/icon';
import { Transaction } from '../../../../models/transaction';
import { ViewTransactionService } from '../../../../services/communication/view-transaction-service';

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

  constructor(private viewTransactionService: ViewTransactionService) {
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
      this.transactionForm.controls['amount'].setValue(transaction.amount);
      this.transactionForm.controls['date'].setValue(transaction.date);
      this.transactionForm.controls['account'].setValue(transaction.account);
      this.transactionForm.controls['notes'].setValue(transaction.notes);

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

  inputTransactionNameBlue() {
    this.editName = false;
  }

  delete() {}

  // todo: add date picker and ability to change transaction date
  // todo: add categories select modal
  // todo: implement form validation logic
  // todo: implement save to service
  // todo: implement delete
}
