import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectAccountCategoryComponent } from './select-account-category.component';

describe('SelectAccountCategoryComponent', () => {
  let component: SelectAccountCategoryComponent;
  let fixture: ComponentFixture<SelectAccountCategoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelectAccountCategoryComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SelectAccountCategoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
