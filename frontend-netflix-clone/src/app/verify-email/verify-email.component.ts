import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AuthServiceService } from '../shared/services/auth-service.service';

@Component({
  selector: 'app-verify-email',
  standalone: false,
  templateUrl: './verify-email.component.html',
  styleUrl: './verify-email.component.css',
})
export class VerifyEmailComponent implements OnInit {
  loading = true;
  success = false;
  message = '';

  constructor(
    private route: ActivatedRoute,
    private authService: AuthServiceService,
  ) {}

  ngOnInit(): void {
    const token = this.route.snapshot.queryParamMap.get('token');

    if (!token) {
      ((this.loading = false),
        (this.success = false),
        (this.message = 'Invalid verification link. No token provided.'));
      return;
    }

    this.authService.verifyEmail(token).subscribe({
      next: (response: any) => {
        ((this.loading = false),
          (this.success = true),
          (this.message =
            response.message ||
            'Email verified successfully! You can now login.'));
      },
      error: (err) => {
        ((this.loading = false),
          (this.success = false),
          (this.message =
            err.error?.error ||
            'Verification failed. The link may have expired or is invalid.'));
      },
    });
  }
}
