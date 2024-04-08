import { Component } from '@angular/core';
import {DatePipe, DecimalPipe, NgForOf, NgIf} from '@angular/common';
import { AutoResizeDirective } from '../../../../directives/auto-resize.directive';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import { MatIcon } from '@angular/material/icon';
import {Transaction} from "../../../../models/transaction";
import {ViewTransactionService} from "../../../../services/communication/view-transaction-service";

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
export class TransactionViewComponent {
  protected isOpened: boolean = false;
  protected transaction!: Transaction;
  protected transactionForm: FormGroup;

  constructor(private viewTransactionService: ViewTransactionService) {
    this.transactionForm = new FormGroup({
      title: new FormControl('', Validators.required),
      amount: new FormControl(Validators.required),
      notes: new FormControl(''),
    });

    this.viewTransactionService.modalOpened$.subscribe((transaction) => {
      this.transaction = transaction;
      this.showModal();
    });
  }

  showModal() {
    this.isOpened = true;
  }

  closeModal() {
    this.isOpened = false;
  }

  delete() {}
}
