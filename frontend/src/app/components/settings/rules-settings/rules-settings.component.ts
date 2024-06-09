import {Component, OnInit} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {RulesSettingsService} from "../../../services/communication/rules-settings-service";
import {RuleService} from "../../../services/api/rule-service";
import {ConditionTypeEnum, Rule} from "../../../models/rule";

@Component({
  selector: 'app-rules-settings',
  standalone: true,
  imports: [
    NgIf,
    MatIcon,
    NgForOf
  ],
  templateUrl: './rules-settings.component.html',
  styleUrl: './rules-settings.component.scss'
})
export class RulesSettingsComponent implements OnInit {

  protected isOpened: boolean = false;
  protected rules: Rule[] = [];

  constructor(private ruleSettingsService: RulesSettingsService,
              private ruleService: RuleService) {

  }

  ngOnInit(): void {
    this.ruleSettingsService.modalOpened$.subscribe(() => {
      this.showModal();
    });

    this.ruleService.getRules().subscribe((rules) => {
      this.rules = rules;
    });
  }

  private showModal() {
    this.isOpened = true;
  }

  closeModal() {
    this.isOpened = false;
  }

  addRule() {

  }

  protected readonly ConditionTypeEnum = ConditionTypeEnum;
}
