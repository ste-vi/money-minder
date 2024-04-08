import { Component } from '@angular/core';
import { NgForOf, NgIf } from '@angular/common';
import { AutoResizeDirective } from '../../../../directives/auto-resize.directive';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import { MatIcon } from '@angular/material/icon';

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
  ],
  templateUrl: './transaction-view.component.html',
  styleUrl: './transaction-view.component.scss',
})
export class TransactionViewComponent {
  protected isOpened: boolean = true;
  protected transactionForm: FormGroup;

  constructor() {
    this.transactionForm = new FormGroup({
      title: new FormControl('', Validators.required),
      amount: new FormControl(Validators.required),
      notes: new FormControl(''),
    });
  }

  closeModal() {
    this.isOpened = false;
  }

  delete() {

  }
}
