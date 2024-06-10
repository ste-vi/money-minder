import {Component, Input, OnInit} from '@angular/core';
import {Account} from "../../../../models/account";
import {NetWorth} from "../../../../models/net-worth";
import {AccountService} from "../../../../services/api/account-service";

@Component({
  selector: 'app-net-worth-widget',
  standalone: true,
  imports: [],
  templateUrl: './net-worth-widget.component.html',
  styleUrl: './net-worth-widget.component.scss'
})
export class NetWorthWidgetComponent {

  protected netWorth: NetWorth | undefined = undefined;

  constructor(private accountsService: AccountService) {
    this.accountsService.getNetWorth().subscribe(netWorth => this.netWorth = netWorth)
  }

}
