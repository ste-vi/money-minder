import { Component, ElementRef, ViewChild } from '@angular/core';
import { DecimalPipe, NgClass, NgForOf, NgIf } from '@angular/common';
import { CreateAccountService } from '../../../services/communication/create-account-service';
import { MatIcon } from '@angular/material/icon';
import { CurrencyService } from '../../../services/api/currency-service';
import { Currency } from '../../../models/currency';
import {
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { AutoResizeDirective } from '../../../directives/auto-resize.directive';
import { AccountService } from '../../../services/api/account-service';
import { Account } from '../../../models/account';

@Component({
  selector: 'app-create-account',
  standalone: true,
  imports: [
    NgIf,
    MatIcon,
    NgForOf,
    FormsModule,
    ReactiveFormsModule,
    NgClass,
    DecimalPipe,
    AutoResizeDirective,
  ],
  templateUrl: './create-account.component.html',
  styleUrl: './create-account.component.scss',
})
export class CreateAccountComponent {
  protected isOpened: boolean = false;
  protected currencies: Currency[] = [];
  protected accountForm: FormGroup;

  // @ts-ignore
  @ViewChild('balanceInput') protected balanceInput: ElementRef;

  constructor(
    private createAccountService: CreateAccountService,
    private accountService: AccountService,
    private currencyService: CurrencyService,
  ) {
    this.accountForm = new FormGroup({
      title: new FormControl('', Validators.required),
      category: new FormControl(Validators.required),
      currency: new FormControl(this.currencies[0], Validators.required),
      balance: new FormControl('0.00', [Validators.required]),
    });

    this.createAccountService.modalOpened$.subscribe((category) => {
      this.showModal();
      this.accountForm.controls['category'].setValue(category);
    });

    this.currencyService.getCurrencies().subscribe((currencies) => {
      this.currencies = currencies;
      this.accountForm.controls['currency'].setValue(currencies[0]);
    });
  }

  private showModal() {
    this.isOpened = true;
  }

  closeModal() {
    this.isOpened = false;
  }

  formatBalance(): void {
    let balanceValue = this.accountForm.controls['balance'].value;
    if (!balanceValue) {
      return;
    }
    balanceValue = parseFloat(balanceValue).toFixed(2);
    this.accountForm.controls['balance'].setValue(balanceValue, {
      emitEvent: false,
    });

    const event = new Event('input', {
      bubbles: true,
      cancelable: true,
    });
    this.balanceInput.nativeElement.dispatchEvent(event);
  }

  createAccount() {
    let account: Account = {
      name: this.accountForm.controls['title'].value,
      currency: this.accountForm.controls['currency'].value,
      balance: parseFloat(
        parseFloat(this.accountForm.controls['balance'].value).toFixed(2),
      ),
    };
    let category = this.accountForm.controls['category'].value;

    this.accountService.createAccount(account, category);

    this.closeModal();
    this.resetForm();
  }

  private resetForm() {
    this.accountForm.reset({
      title: '',
      category: Validators.required,
      currency: this.currencies[0],
      balance: '0.00',
    });
  }
}
