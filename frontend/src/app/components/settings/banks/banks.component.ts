import { Component } from '@angular/core';
import { LoaderComponent } from '../../common/loader/loader.component';
import { MatIcon } from '@angular/material/icon';
import { NgForOf, NgIf } from '@angular/common';
import { ViewBanksService } from '../../../services/communication/view-banks-service';
import { SelectNewBankService } from '../../../services/communication/select-new-bank-service';
import { SelectNewBankComponent } from './select-new-bank/select-new-bank.component';
import { BanksService } from '../../../services/api/banks-service';
import { Bank } from '../../../models/bank';

@Component({
  selector: 'app-banks',
  standalone: true,
  imports: [LoaderComponent, MatIcon, NgForOf, NgIf, SelectNewBankComponent],
  templateUrl: './banks.component.html',
  styleUrl: './banks.component.scss',
})
export class BanksComponent {
  protected isOpened: boolean = false;
  protected banks: Bank[] = [];

  constructor(
    private viewBanksService: ViewBanksService,
    private selectNewBankService: SelectNewBankService,
    private bankService: BanksService,
  ) {
    this.viewBanksService.modalOpened$.subscribe(() => {
      this.isOpened = true;
      this.getBanks();
    });
    this.bankService.refreshBanks$.subscribe(() => {
      this.getBanks();
    });
  }

  private getBanks() {
    this.bankService.getBanks().subscribe((banks) => (this.banks = banks));
  }

  closeModal() {
    this.isOpened = false;
  }

  openConnectBankModal() {
    this.selectNewBankService.openModal();
  }
}
