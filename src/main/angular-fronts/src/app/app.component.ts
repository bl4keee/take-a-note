import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { BehaviorSubject, Observable, of, Subject } from 'rxjs';
import { catchError, map, startWith } from 'rxjs/operators';
import { DataState } from './enum/datastate';
import { Level } from './enum/level.enum';
import { AppState } from './interface/appstate';
import { CustomHttpResponse } from './interface/custom-http-response';
import { Note } from './interface/note-interface';
import { NoteService } from './service/note.service';
import { NotificationService } from './service/notification.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AppComponent implements OnInit {

  appState$: Observable<AppState<CustomHttpResponse>> | undefined;
  readonly Level = Level;
  readonly DataState = DataState;
  private dataSubject = new BehaviorSubject<CustomHttpResponse | undefined>(undefined);
  private isLoadingSubject = new BehaviorSubject<boolean>(false);
  isLoading$ = this.isLoadingSubject.asObservable();
  private selectedNoteSubject = new Subject<Note>();
  selectedNote$ = this.selectedNoteSubject.asObservable();
  private filteredSubject = new BehaviorSubject<Level>(Level.ALL);
  filteredLevel$ = this.filteredSubject.asObservable();

  constructor(private noteService: NoteService, private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.appState$ = this.noteService.getAllNotes$
      .pipe(
        map(response => {
          this.dataSubject.next(response);
          this.notificationService.onSuccess(response.message);
          this.filteredSubject.next(Level.ALL);
          return { dataState: DataState.LOADED, data: response }
        }),
        startWith({ dataState: DataState.LOADING }),
        catchError((error: string) => {
          this.notificationService.onError(error);
          return of({ dataState: DataState.ERROR, error: error })
        })
      );
  }

  createNote(noteForm: NgForm): void {
    this.isLoadingSubject.next(true);
    this.appState$ = this.noteService.createNote$(noteForm.value)
      .pipe(
        map(response => {
          this.dataSubject
            .next(<CustomHttpResponse>{ ...response, data: [response.data![0], ...this.dataSubject.value!.data!] });
          document.getElementById('closeModal').click();
          noteForm.reset({ title: '', description: '', level: this.Level.HIGH });
          this.isLoadingSubject.next(false);
          this.filteredSubject.next(Level.ALL);
          this.notificationService.onSuccess(response.message);
          return { dataState: DataState.LOADED, data: this.dataSubject.value }
        }),
        startWith({ dataState: DataState.LOADED, data: this.dataSubject.value }),
        catchError((error: string) => {
          this.isLoadingSubject.next(false);
          this.notificationService.onError(error);
          return of({ dataState: DataState.ERROR, error: error })
        })
      );
  }

  updateNote(note: Note): void {
    this.isLoadingSubject.next(true);
    this.appState$ = this.noteService.updateNote$(note)
      .pipe(
        map(response => {
          this.dataSubject.value!.data![this.dataSubject.value.data
            .findIndex(note => note.id === response.data[0].id)] = response.data[0];
          this.dataSubject
            .next(<CustomHttpResponse>{ ...response, data: this.dataSubject.value!.data! });
          document.getElementById('closeModalEdit').click();
          this.filteredSubject.next(Level.ALL);
          this.isLoadingSubject.next(false);
          this.notificationService.onError(response.message);
          return { dataState: DataState.LOADED, data: this.dataSubject.value }
        }),
        startWith({ dataState: DataState.LOADED, data: this.dataSubject.value }),
        catchError((error: string) => {
          this.isLoadingSubject.next(false);
          this.notificationService.onError(error);
          return of({ dataState: DataState.ERROR, error: error })
        })
      );
  }

  filterNotes(level: Level): void {
    this.filteredSubject.next(level);
    this.appState$ = this.noteService.filterNotes$(level, this.dataSubject.value)
      .pipe(
        map(response => {
          this.notificationService.onSuccess(response.message);
          return { dataState: DataState.LOADED, data: response }
        }),
        startWith({ dataState: DataState.LOADED, data: this.dataSubject.value }),
        catchError((error: string) => {
          this.notificationService.onError(error);
          return of({ dataState: DataState.ERROR, error: error })
        })
      );
  }

  deleteNote(noteId: number): void {
    this.appState$ = this.noteService.deleteNote$(noteId)
      .pipe(
        map(response => {
          this.dataSubject
            .next(<CustomHttpResponse>{ ...response, data: this.dataSubject.value!.data!.filter(note => note.id !== response.data[0].id) });
            this.notificationService.onSuccess(response.message);
            this.filteredSubject.next(Level.ALL);
            this.isLoadingSubject.next(false);
          return { dataState: DataState.LOADED, data: this.dataSubject.value }
        }),
        startWith({ dataState: DataState.LOADED, data: this.dataSubject.value }),
        catchError((error: string) => {
          this.notificationService.onError(error);
          return of({ dataState: DataState.ERROR, error: error })
        })
      );
  }

  selectNote(note: Note): void {
    this.selectedNoteSubject.next(note);
    document.getElementById('editNoteButton').click();
  }
}
